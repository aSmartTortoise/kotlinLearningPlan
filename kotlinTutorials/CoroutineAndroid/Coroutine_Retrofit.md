Coroutine + Retrofit 封装网络请求

# 参考文章 

1 [Retrofit+Kotlin Coroutine重构Android网络框架](https://blog.csdn.net/taotao110120119/article/details/110878525)

2 [OkHttp踩坑记：为何 response.body().string() 只能调用一次？](https://juejin.cn/post/6844903545628524551)

# 1  定义实体类

本文以wanandroid项目服务器提供的接口https://www.wanandroid.com/user/login为例来描述客户端需要定义的实体类。

服务器返回的数据格式如下：

```json
{
    "data": {
        "admin": false,
        "chapterTops": [],
        "coinCount": 126,
        "collectIds": [
            26980
        ],
        "email": "",
        "icon": "",
        "id": 150842,
        "nickname": "13163268087",
        "password": "",
        "publicName": "13163268087",
        "token": "",
        "type": 0,
        "username": "13163268087"
    },
    "errorCode": 0,
    "errorMsg": ""
}
```

## 1.1 定义的实体类如下

```k
data class BaseResponse<T>(val errorCode: Int, val errorMsg: String, val data: T)

data class User (
    val id: Int,
    val username: String?,
    val nickname: String?,
    val token: String?,
    val icon: String?,
    val email: String?,
    val password: String?,
    val signatrue: String?,
    val sex: String?,
    val birthday: String?
)
```

另外我们要考虑到服务器返回的业务数据出错的情况，针对本例，即json数据的errorCode不为0，且data字段为null的情况。服务器返回成功的数据用BaseResponse定义，服务器返回失败的数据，可以用自定义的Failure结构体定义，该结构体包含了errorCode和errorMsg两个字段。为此我们定义如下的实体类。

```k
sealed class ApiResult<out T> {
    data class Success<out T>(val data: T):ApiResult<T>()
    data class Failure(val errorCode:Int, val errorMsg:String):ApiResult<Nothing>()
}
```

同时定义了业务异常的实体类，如下：

```k
/**
 * 客户端本地定义的网络请求的 errorCode 和 errorMsg
 *
 * 这里的errorCode < 0，为了与服务器返回的errorCode做区分，服务器返回的 errorCode > 0
 */
object ApiError {
    //数据是null
    val dataIsNull = Error(-1,"data is null")
    //http status code 不是 成功
    val httpStatusCodeError = Error(-2,"Server error. Please try again later.")
    //未知异常
    val unknownException = Error(-3,"unknown exception")
}

data class Error(val errorCode: Int, val errorMsg: String)
```

# 2 Retrofit实例的获取与配置

## 2.1 定义api接口

```k
interface ApiInterface {
    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     */
    @FormUrlEncoded
    @POST("/user/login")
    suspend fun login2(
        @Field("username") username: String,
        @Field("password") password: String
    ): ApiResult<BaseResponse<User>>
    
}
```

/user/login 该接口请求返回的数据类型是ApiResult<BaseResponse<User>>，是挂起函数，suspend。该接口的实例需要是单例的。如下定义获取ApiInterface的实例。

```k
object HttpManager {
    val service: ApiInterface by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        build()
    }

    private fun build(): ApiInterface {
    	// 1 log 拦截器。
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("HttpManager", "log: wyj message:$message")
        }
        if (BuildConfig.DEBUG) {
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            loggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
        }
        // 2 OkHttp的缓存设置，缓存目录和缓存大小。
        val cache = Cache(
            File(MyApplication.application.cacheDir, "cache"),
            1024 * 1024 * 50
        )
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(HeaderInterceptor()) // 3 读取cookie的缓存，在请求头部设置cookie的字段。
            .addInterceptor(CookieInterceptor()) //4 将服务器返回的cookie存到本地
            .addInterceptor(BusinessErrorInterceptor()) // 5 服务器返回错误的业务数据，进行拦截以Failure的形式回掉
            .cache(cache)
            .build()
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl("https://www.wanandroid.com")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(ApiResultCallAdapterFactory())// 6 将Call转换为自定义数据类型的CallAdapter
            .build().run {
                create(ApiInterface::class.java)
            }
    }
}
```

## 2.2 服务器返回错误的业务数据的处理

服务器返回错误的业务数据指的是服务器相应状态码仍然是200，但是业务数据是错误的，本地而言，errorCode不为0，且data数据为null，这种场景，可以通过定义BusinessErrorInterceptor拦截返回的response，解析业务数据，判断为错误的业务数据后，抛出异常，从而结束请求，回调Callback的onFailure函数。可以定义一个继承IOException的异常来，并封装errorCode和errorMsg字段。

```k
class BusinessErrorInterceptor :Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {

        var response = chain.proceed(chain.request())
        //1 http status不是成功的情况下，我们不处理
        if (!response.isSuccessful){
            return response
        }        

        val responseBody = response.body()!!
        val source = responseBody.source()
        source.request(Long.MAX_VALUE) // Buffer the entire body.
        var buffer = source.buffer
        val contentType = responseBody.contentType()
        val charset: Charset = contentType?.charset(UTF_8) ?: UTF_8
        val resultString = buffer.clone().readString(charset)

        val jsonObject = JSONObject(resultString)
        if (!jsonObject.has("errorCode")) {
            return response
        }

        val errorCode = jsonObject.optInt("errorCode", -1000)
        //2 对于业务成功的情况不做处理
        if (errorCode == 0) {
            return response
        }
        val errorMsg = jsonObject.optString("errorMsg", "some error msg")
        // 3 抛出异常，结束网络请求，回调Callback#onFailure函数。
        throw ApiException(errorCode, errorMsg)
    }

}
```

ApiException需要继承IOException，只有IOException才会由Interceptor自定义并回调Callback#onFailure函数。

```k
class ApiException(val errorCode:Int,val errorMsg:String): IOException()
```

以下是OkHttp库中的RealCall的代码。

```java
 final class RealCall implements Call {
     ...
	final class AsyncCall extends NamedRunnable {
      ...
    @Override protected void execute() {
      boolean signalledCallback = false;
      transmitter.timeoutEnter();
      try {
        Response response = getResponseWithInterceptorChain();
        signalledCallback = true;
        responseCallback.onResponse(RealCall.this, response);
      } catch (IOException e) {// 1 拦截器中抛出的IOException 才能自定义。
        if (signalledCallback) {
          // Do not signal the callback twice!
          Platform.get().log(INFO, "Callback failure for " + toLoggableString(), e);
        } else {
          responseCallback.onFailure(RealCall.this, e);
        }
      } catch (Throwable t) { // 2 拦截器中的非IOException，返回给Callback#onFailure函数的异常类型已写死。
        cancel();
        if (!signalledCallback) {
          IOException canceledException = new IOException("canceled due to " + t);
          canceledException.addSuppressed(t);
          responseCallback.onFailure(RealCall.this, canceledException);
        }
        throw t;
      } finally {
        client.dispatcher().finished(this);
      }
    }
  }
  ...   
 }

```

## 2.3 自定义CallAdapter

http响应的数据类型为T，本例中对应BaseResponse<User>，CallAdapter可以将自定义的Call<ApiResult<BaseResponse<User>>>和Call<BaseResponse<User>>关联起来，前者的接口实现完全委托给后者，从而使得接口的返回值为自定义的ApiResult<BaseResponse<User>>，即在BaseResponse<User>基础上包装了一层。

如下CallAdapter的源码：

```java
/**
 * Adapts a {@link Call} with response type {@code R} into the type of {@code T}. Instances are
 * created by {@linkplain Factory a factory} which is {@linkplain
 * Retrofit.Builder#addCallAdapterFactory(Factory) installed} into the {@link Retrofit} instance.
 */
public interface CallAdapter<R, T> {
  /**
   * Returns the value type that this adapter uses when converting the HTTP response body to a Java
   * object. For example, the response type for {@code Call<Repo>} is {@code Repo}. This type is
   * used to prepare the {@code call} passed to {@code #adapt}.
   *
   * <p>Note: This is typically not the same type as the {@code returnType} provided to this call
   * adapter's factory.
   */
  Type responseType();

  /**
   * Returns an instance of {@code T} which delegates to {@code call}.
   *
   * <p>For example, given an instance for a hypothetical utility, {@code Async}, this instance
   * would return a new {@code Async<R>} which invoked {@code call} when run.
   *
   * <pre><code>
   * &#64;Override
   * public &lt;R&gt; Async&lt;R&gt; adapt(final Call&lt;R&gt; call) {
   *   return Async.create(new Callable&lt;Response&lt;R&gt;&gt;() {
   *     &#64;Override
   *     public Response&lt;R&gt; call() throws Exception {
   *       return call.execute();
   *     }
   *   });
   * }
   * </code></pre>
   */
  T adapt(Call<R> call);

  /**
   * Creates {@link CallAdapter} instances based on the return type of {@linkplain
   * Retrofit#create(Class) the service interface} methods.
   */
  abstract class Factory {
    /**
     * Returns a call adapter for interface methods that return {@code returnType}, or null if it
     * cannot be handled by this factory.
     */
    public abstract @Nullable CallAdapter<?, ?> get(
        Type returnType, Annotation[] annotations, Retrofit retrofit);

    /**
     * Extract the upper bound of the generic parameter at {@code index} from {@code type}. For
     * example, index 1 of {@code Map<String, ? extends Runnable>} returns {@code Runnable}.
     */
    protected static Type getParameterUpperBound(int index, ParameterizedType type) {
      return Utils.getParameterUpperBound(index, type);
    }

    /**
     * Extract the raw class type from {@code type}. For example, the type representing {@code
     * List<? extends Runnable>} returns {@code List.class}.
     */
    protected static Class<?> getRawType(Type type) {
      return Utils.getRawType(type);
    }
  }
}
```

CallAdapter.Factory 是根据定义的接口返回值类型（ApiResult<BaseResponse<User>>）得到自定义的CallAdapter的实例。

自定义CallAdapter、CallAdapter.Factory以及自定义的Call如下：

```k
class ApiResultCallAdapterFactory : CallAdapter.Factory() {

    override fun get(
        returnType: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        check(getRawType(returnType) == Call::class.java) { "$returnType must be retrofit2.Call." }
        check(returnType is ParameterizedType) { "$returnType must be parameterized. Raw types are not supported" }

        //取出Call<T> 里的T，检查是否是ApiResult<T>
        val apiResultType = getParameterUpperBound(0, returnType)
        check(getRawType(apiResultType) == ApiResult::class.java) { "$apiResultType must be ApiResult." }
        check(apiResultType is ParameterizedType) { "$apiResultType must be parameterized. Raw types are not supported" }

        //取出ApiResult<T>中的T 也就是API返回数据对应的数据类型
        val dataType = getParameterUpperBound(0, apiResultType)

        return ApiResultCallAdapter<Any>(dataType)
    }

}

class ApiResultCallAdapter<T>(private val type: Type) : CallAdapter<T, Call<ApiResult<T>>> {
    override fun responseType(): Type = type

    override fun adapt(call: Call<T>): Call<ApiResult<T>> {
        return ApiResultCall(call)
    }
}

class ApiResultCall<T>(private val delegate: Call<T>) : Call<ApiResult<T>> {
    /**
     * 该方法会被Retrofit处理suspend方法的代码调用，并传进来一个callback,如果你回调了callback.onResponse，那么suspend方法就会成功返回
     * 如果你回调了callback.onFailure那么suspend方法就会抛异常
     *
     * 所以我们这里的实现是永远回调callback.onResponse,只不过在请求成功的时候返回的是ApiResult.Success对象，
     * 在失败的时候返回的是ApiResult.Failure对象，这样外面在调用suspend方法的时候就不会抛异常，一定会返回ApiResult.Success 或 ApiResult.Failure
     */
    override fun enqueue(callback: Callback<ApiResult<T>>) {
        //delegate 是用来做实际的网络请求的Call<T>对象，为OkHttpCall，网络请求的成功失败会回调不同的方法
        delegate.enqueue(object : Callback<T> {

            /**
             * 网络请求成功返回，会回调该方法（无论status code是不是200）
             */
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {//http status 是200+
                    //这里担心response.body()可能会为null(还没有测到过这种情况)，所以做了一下这种情况的处理，
                    // 处理了这种情况后还有一个好处是我们就能保证我们传给ApiResult.Success的对象就不是null，这样外面用的时候就不用判空了
                    val apiResult = if (response.body() == null) {
                        ApiResult.Failure(
                            ApiError.dataIsNull.errorCode,
                            ApiError.dataIsNull.errorMsg
                        )
                    } else {
                        ApiResult.Success(response.body()!!)
                    }
                    callback.onResponse(this@ApiResultCall, Response.success(apiResult))
                } else {//http status错误
                    val failureApiResult = ApiResult.Failure(
                        ApiError.httpStatusCodeError.errorCode,
                        ApiError.httpStatusCodeError.errorMsg
                    )
                    callback.onResponse(this@ApiResultCall, Response.success(failureApiResult))
                }

            }

            /**
             * 在网络请求中发生了异常，会回调该方法
             *
             * 对于网络请求成功，但是业务失败的情况，我们也会在对应的Interceptor中抛出异常，这种情况也会回调该方法
             */
            override fun onFailure(call: Call<T>, t: Throwable) {
                val failureApiResult =
                    if (t is ApiException) {//Interceptor里会通过throw ApiException 来直接结束请求 同时ApiException里会包含错误信息
                        ApiResult.Failure(t.errorCode, t.errorMsg)
                    } else {
                        ApiResult.Failure(
                            ApiError.unknownException.errorCode,
                            ApiError.unknownException.errorMsg
                        )
                    }

                callback.onResponse(this@ApiResultCall, Response.success(failureApiResult))
            }

        })
    }

    override fun clone(): Call<ApiResult<T>> = ApiResultCall(delegate.clone())

    override fun execute(): Response<ApiResult<T>> {
        throw UnsupportedOperationException("ApiResultCall does not support synchronous execution")
    }


    override fun isExecuted(): Boolean {
        return delegate.isExecuted
    }

    override fun cancel() {
        delegate.cancel()
    }

    override fun isCanceled(): Boolean {
        return delegate.isCanceled
    }

    override fun request(): Request {
        return delegate.request()
    }

    override fun timeout(): Timeout {
        return delegate.timeout()
    }
}
```

(1) 重新定义了自定义泛型参数的Call类 ApiResultCall，泛型参数类型为接口的返回直接类型ApiResult<BaseResponse<User>>，ApiResultCall的实现完全委托给Call<BaseResponse<User>>，比如重写了enqueue、execute等核心方法。

(2) 重新定义了CallAdapter 为ApiResultCallAdapter，用来关联ApiResultCall<ApiResult<BaseResponse<User>>>和Call<BaseResponse<User>>，后者的实例类型为OkHttpCall（下文会分析）。

(3) 重新定义了CallAdapter.Factory，为ApiResultCallAdapterFactory。用来构造自定义的CallAdapter实例。

当Retrofit加载接口时候，会解析接口中声明的注解，解析过程中会根据Retrofit配置的CallAdapter.Factory来构建CallAdapter，进而关联两个Call。

### 2.3.1 自定义Call的enqueue方法的实现

ApiResultCall<ApiResult<BaseResponse<User>>>的enqueue方法完全委托给OkHttpCall<BaseResponse<User>>。

```k
class ApiResultCall<T>(private val delegate: Call<T>) : Call<ApiResult<T>> {
    /**
     * 该方法会被Retrofit处理suspend方法的代码调用，并传进来一个callback,如果回调了callback.onResponse，那么suspend方法就会成功返回
     * 如果回调了callback.onFailure那么suspend方法就会抛异常
     *
     * 所以这里的实现是永远回调callback.onResponse,只不过在请求成功的时候返回的是ApiResult.Success对象，
     * 在失败的时候返回的是ApiResult.Failure对象，这样外面在调用suspend方法的时候就不会抛异常，一定会返回ApiResult.Success 或 ApiResult.Failure
     */
    override fun enqueue(callback: Callback<ApiResult<T>>) {
        //delegate 是用来做实际的网络请求的Call<T>对象，为OkHttpCall，网络请求的成功失败会回调不同的方法
        delegate.enqueue(object : Callback<T> {

            /**
             * 网络请求成功返回，会回调该方法（无论status code是不是200）
             */
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {//http status 是200+
                    //这里担心response.body()可能会为null(还没有测到过这种情况)，所以做了一下这种情况的处理，
                    // 处理了这种情况后还有一个好处是我们就能保证我们传给ApiResult.Success的对象就不是null，这样外面用的时候就不用判空了
                    val apiResult = if (response.body() == null) {
                        ApiResult.Failure(
                            ApiError.dataIsNull.errorCode,
                            ApiError.dataIsNull.errorMsg
                        )
                    } else {
                        ApiResult.Success(response.body()!!)
                    }
                    callback.onResponse(this@ApiResultCall, Response.success(apiResult))
                } else {//http status错误
                    val failureApiResult = ApiResult.Failure(
                        ApiError.httpStatusCodeError.errorCode,
                        ApiError.httpStatusCodeError.errorMsg
                    )
                    callback.onResponse(this@ApiResultCall, Response.success(failureApiResult))
                }

            }

            /**
             * 在网络请求中发生了异常，会回调该方法
             * 对于网络请求成功，但是业务失败的情况，我们也会在对应的Interceptor中抛出异常，这种情况也会回调该方法
             */
            override fun onFailure(call: Call<T>, t: Throwable) {
                val failureApiResult =
                    if (t is ApiException) {//Interceptor里会通过throw ApiException 来直接结束请求 同时ApiException里会包含错误信息
                        ApiResult.Failure(t.errorCode, t.errorMsg)
                    } else {
                        ApiResult.Failure(
                            ApiError.unknownException.errorCode,
                            ApiError.unknownException.errorMsg
                        )
                    }

                callback.onResponse(this@ApiResultCall, Response.success(failureApiResult))
            }

        })
    }
    
}
```

# 3 Retrofit加载接口的流程

Retrofit加载调用对应的接口会先加载接口然后调用。该操作会执行到如下代码：

## 3.1 Retrofit#create

```java
  public <T> T create(final Class<T> service) {
    validateServiceInterface(service);
    return (T)
        Proxy.newProxyInstance(
            service.getClassLoader(),
            new Class<?>[] {service},
            new InvocationHandler() {
              private final Platform platform = Platform.get();
              private final Object[] emptyArgs = new Object[0];

              @Override
              public @Nullable Object invoke(Object proxy, Method method, @Nullable Object[] args)
                  throws Throwable {
                // If the method is a method from Object then defer to normal invocation.
                if (method.getDeclaringClass() == Object.class) {
                  return method.invoke(this, args);
                }
                args = args != null ? args : emptyArgs;
                  // 1 当接口实例调用对应的接口会执行loadServiceMethod(method).invoke(args)，先加载接口，然后调用。
                return platform.isDefaultMethod(method)
                    ? platform.invokeDefaultMethod(method, service, proxy, args)
                    : loadServiceMethod(method).invoke(args);
              }
            });
  }
```

Retrofit通过create构建接口实例的过程中会构建代理对象得到InvocationHandler，当通过接口实例调用对应的接口时候会触发invoke方法。然后加载对应的接口并调用。

## 3.2 Retrofit#loadServiceMethod

```java
  ServiceMethod<?> loadServiceMethod(Method method) {
    // 1如果缓存中有以加载好的ServiceMethod实例则直接返回
    ServiceMethod<?> result = serviceMethodCache.get(method);
    if (result != null) return result;

    synchronized (serviceMethodCache) {
      result = serviceMethodCache.get(method);
      if (result == null) {
        // 2 解析对应的方法，得到ServiceMethod实例后，缓存起来。
        result = ServiceMethod.parseAnnotations(this, method);
        serviceMethodCache.put(method, result);
      }
    }
    return result;
  }
```

如果缓存中有以加载好的ServiceMethod实例则直接返回。否则先解析对应的方法，得到ServiceMethod实例后，缓存起来以便以后重复利用。

## 3.3 ServiceMethod.parseAnnotations

```java
  static <T> ServiceMethod<T> parseAnnotations(Retrofit retrofit, Method method) {
    RequestFactory requestFactory = RequestFactory.parseAnnotations(retrofit, method);

    Type returnType = method.getGenericReturnType();
    if (Utils.hasUnresolvableType(returnType)) {
      throw methodError(
          method,
          "Method return type must not include a type variable or wildcard: %s",
          returnType);
    }
    if (returnType == void.class) {
      throw methodError(method, "Service methods cannot return void.");
    }

    return HttpServiceMethod.parseAnnotations(retrofit, method, requestFactory);
  }
```

会调用HttpServiceMethod.parseAnnotations静态方法得到HttpServiceMethod的实例。

## 3.4 HttpServiceMethod#parseAnnotations

```java
  static <ResponseT, ReturnT> HttpServiceMethod<ResponseT, ReturnT> parseAnnotations(
      Retrofit retrofit, Method method, RequestFactory requestFactory) {
    // 1 接口如果声明的是suspend则isKotlinSuspendFunction为true。
    boolean isKotlinSuspendFunction = requestFactory.isKotlinSuspendFunction;
    boolean continuationWantsResponse = false;
    boolean continuationBodyNullable = false;

    Annotation[] annotations = method.getAnnotations();
    Type adapterType;
    if (isKotlinSuspendFunction) {
      Type[] parameterTypes = method.getGenericParameterTypes();
      // 2 responseType为ApiResult<BaseResponse<User>>
      Type responseType =
          Utils.getParameterLowerBound(
              0, (ParameterizedType) parameterTypes[parameterTypes.length - 1]);
      if (getRawType(responseType) == Response.class && responseType instanceof ParameterizedType) {
        // Unwrap the actual body type from Response<T>.
        responseType = Utils.getParameterUpperBound(0, (ParameterizedType) responseType);
        continuationWantsResponse = true;
      } else {
        // TODO figure out if type is nullable or not
        // Metadata metadata = method.getDeclaringClass().getAnnotation(Metadata.class)
        // Find the entry for method
        // Determine if return type is nullable or not
      }

      adapterType = new Utils.ParameterizedTypeImpl(null, Call.class, responseType);
      annotations = SkipCallbackExecutorImpl.ensurePresent(annotations);
    } else {
      adapterType = method.getGenericReturnType();
    }

    // 3 创建CallAdapter。触发CallAdapter.Factory的构建CallAdapter的流程。
    CallAdapter<ResponseT, ReturnT> callAdapter =
        createCallAdapter(retrofit, method, adapterType, annotations);
    // 4 responseType为BaseResponse<User>
    Type responseType = callAdapter.responseType();
    if (responseType == okhttp3.Response.class) {
      throw methodError(
          method,
          "'"
              + getRawType(responseType).getName()
              + "' is not a valid response body type. Did you mean ResponseBody?");
    }
    if (responseType == Response.class) {
      throw methodError(method, "Response must include generic type (e.g., Response<String>)");
    }
    // TODO support Unit for Kotlin?
    if (requestFactory.httpMethod.equals("HEAD") && !Void.class.equals(responseType)) {
      throw methodError(method, "HEAD method must use Void as response type.");
    }

    Converter<ResponseBody, ResponseT> responseConverter =
        createResponseConverter(retrofit, method, responseType);

    okhttp3.Call.Factory callFactory = retrofit.callFactory;
    if (!isKotlinSuspendFunction) {
      return new CallAdapted<>(requestFactory, callFactory, responseConverter, callAdapter);
    } else if (continuationWantsResponse) {
      //noinspection unchecked Kotlin compiler guarantees ReturnT to be Object.
      return (HttpServiceMethod<ResponseT, ReturnT>)
          new SuspendForResponse<>(
              requestFactory,
              callFactory,
              responseConverter,
              (CallAdapter<ResponseT, Call<ResponseT>>) callAdapter);
    } else {
      //noinspection unchecked Kotlin compiler guarantees ReturnT to be Object.
      // 5 构建SuspendForBody。
      return (HttpServiceMethod<ResponseT, ReturnT>)
          new SuspendForBody<>(
              requestFactory,
              callFactory,
              responseConverter,
              (CallAdapter<ResponseT, Call<ResponseT>>) callAdapter,
              continuationBodyNullable);
    }
  }
```

HttpServiceMethod#parseAnnotations方法的执行流程。

(1) 判断接口方法是否是suspend方法。

(2) 根据方法得到返回值类型。

(3) 由Retrofit配置的CallAdapter.Factory构建CallAdapter实例。

(4) 构建SuspendForBody的实例该类是ServiceMethod的派生类。

## 3.5 Retrofit构建CallAdapter

```java
  public CallAdapter<?, ?> callAdapter(Type returnType, Annotation[] annotations) {
    return nextCallAdapter(null, returnType, annotations);
  }

  public CallAdapter<?, ?> nextCallAdapter(
      @Nullable CallAdapter.Factory skipPast, Type returnType, Annotation[] annotations) {
    ...

    int start = callAdapterFactories.indexOf(skipPast) + 1;
    // 1 遍历配置的CallAdapter.Factory集合，由Factory构建CallAdapter实例。
    for (int i = start, count = callAdapterFactories.size(); i < count; i++) {
      CallAdapter<?, ?> adapter = callAdapterFactories.get(i).get(returnType, annotations, this);
      if (adapter != null) {
        return adapter;
      }
    }

    StringBuilder builder =
        new StringBuilder("Could not locate call adapter for ").append(returnType).append(".\n");
    if (skipPast != null) {
      builder.append("  Skipped:");
      for (int i = 0; i < start; i++) {
        builder.append("\n   * ").append(callAdapterFactories.get(i).getClass().getName());
      }
      builder.append('\n');
    }
    builder.append("  Tried:");
    for (int i = start, count = callAdapterFactories.size(); i < count; i++) {
      builder.append("\n   * ").append(callAdapterFactories.get(i).getClass().getName());
    }
    throw new IllegalArgumentException(builder.toString());
  }
```

遍历配置的CallAdapter.Factory集合，由Factory构建CallAdapter实例。针对本例，会调用ApiResultCallAdapterFactory#get方法构建

ApiResultCallAdapter实例。

## 3.6 ServiceMethod#invoke

得到SuspendForBody对象后随即执行invoke方法。

```java
abstract class HttpServiceMethod<ResponseT, ReturnT> extends ServiceMethod<ReturnT> {
  ...
  @Override
  final @Nullable ReturnT invoke(Object[] args) {
    1 构建OkHttpCall对象，然后执行adapt方法。
    Call<ResponseT> call = new OkHttpCall<>(requestFactory, args, callFactory, responseConverter);
    return adapt(call, args);
  }
    
  ...
  static final class SuspendForBody<ResponseT> extends HttpServiceMethod<ResponseT, Object> {
    ...
    @Override
    protected Object adapt(Call<ResponseT> call, Object[] args) {
      // 1 调用CallAdapter#adapt方法关联 Call<ApiResult<BaseResponse<User>>>和OkHttpCall<BaseResponse<User>>
      call = callAdapter.adapt(call);

      //noinspection unchecked Checked by reflection inside RequestFactory.
      Continuation<ResponseT> continuation = (Continuation<ResponseT>) args[args.length - 1];
      ...
      // 2 isNullable为false，调用KotlinExtensions.await
      try {
        return isNullable
            ? KotlinExtensions.awaitNullable(call, continuation)
            : KotlinExtensions.await(call, continuation);
      } catch (Exception e) {
        return KotlinExtensions.suspendAndThrow(e, continuation);
      }
    }
  }  
}
```

ServiceMethod#invoke方法执行流程如下：

(1) 调用CallAdapter#adapt方法将自定义的Call和OkHttpCall关联起来。

## 3.7 KotlinExtensions

```k
suspend fun <T : Any> Call<T>.await(): T {
  return suspendCancellableCoroutine { continuation ->
    continuation.invokeOnCancellation {
      cancel()
    }
    // 1 会调用自定义的ApiResultCall的enqueue方法。
    enqueue(object : Callback<T> {
      override fun onResponse(call: Call<T>, response: Response<T>) {
        // 2 如果响应码是200，本例而言isSuccessful始终为true，见2.3.1分析。
        if (response.isSuccessful) {
          // 3 如果body为null则抛出KotlinNullPointerException异常，本例body不会为null。见2.3.1分析。
          val body = response.body()
          if (body == null) {
            val invocation = call.request().tag(Invocation::class.java)!!
            val method = invocation.method()
            val e = KotlinNullPointerException("Response from " +
                method.declaringClass.name +
                '.' +
                method.name +
                " was null but response body type was declared as non-null")
            continuation.resumeWithException(e)
          } else {
            // 4 返回值为ApiResult
            continuation.resume(body)
          }
        } else {
          // 5 抛出HttpException的异常。
          continuation.resumeWithException(HttpException(response))
        }
      }

      override fun onFailure(call: Call<T>, t: Throwable) {
        // 6 抛出异常。
        continuation.resumeWithException(t)
      }
    })
  }
}
```

由自定义的ApiResultCall调用enqueue函数，将请求交给OkHttpCall执行。执行完的结果会通过Callback的回调函数回调，并得到返回值

ApiResult或抛出异常。本例而言不会抛出异常。

## 3.8 OkHttpCall执行异步网络请求

```java
  @Override
  public void enqueue(final Callback<T> callback) {
    ...
    okhttp3.Call call;
    Throwable failure;

    synchronized (this) {
      if (executed) throw new IllegalStateException("Already executed.");
      executed = true;

      call = rawCall;
      failure = creationFailure;
      if (call == null && failure == null) {
        try {
          // 1 构建okhttp3.Call实例。
          call = rawCall = createRawCall();
        } catch (Throwable t) {
          throwIfFatal(t);
          failure = creationFailure = t;
        }
      }
    }

    if (failure != null) {
      callback.onFailure(this, failure);
      return;
    }
	// 1 如果取消了，执行Call的cancel方法。
    if (canceled) {
      call.cancel();
    }
	
    // 2 okhttp3.Call实例的enqueue方法。这里就是OkHttp库的核心代码了。
    call.enqueue(
        new okhttp3.Callback() {
          @Override
          public void onResponse(okhttp3.Call call, okhttp3.Response rawResponse) {
            Response<T> response;
            try {
              response = parseResponse(rawResponse);
            } catch (Throwable e) {
              throwIfFatal(e);
              callFailure(e);
              return;
            }

            try {
              // 3 会回调到自定义ApiResultCall的enqueue方法的delegate的Call的回调。见2.3.1分析
              callback.onResponse(OkHttpCall.this, response);
            } catch (Throwable t) {
              throwIfFatal(t);
              t.printStackTrace(); // TODO this is not great
            }
          }

          @Override
          public void onFailure(okhttp3.Call call, IOException e) {
            callFailure(e);
          }

          private void callFailure(Throwable e) {
            try {
               // 4 会回调到自定义ApiResultCall的enqueue方法的delegate的Call的回调。见2.3.1分析
              callback.onFailure(OkHttpCall.this, e);
            } catch (Throwable t) {
              throwIfFatal(t);
              t.printStackTrace(); // TODO this is not great
            }
          }
        });
  }

  private okhttp3.Call createRawCall() throws IOException {
    okhttp3.Call call = callFactory.newCall(requestFactory.create(args));
    if (call == null) {
      throw new NullPointerException("Call.Factory returned null.");
    }
    return call;
  }
```



```jav
public class OkHttpClient implements Cloneable, Call.Factory, WebSocket.Factory {
  ...
  @Override public Call newCall(Request request) {
    // 1 构建RealCall对象。
    return RealCall.newRealCall(this, request, false /* for web socket */);
  }
  ...
}
```

被委托的OkHttpCall在执行异步网络请求过程中，先获取RealCall对象，然后调用enqueue函数执行网络请求。

## 3.9 小节

Retrofit加载ApiInterface定义的suspend的函数并调用的流程如下：

(1) Retrofit通过create方法构建接口实例。过程中会构建代理对象得到InvocationHandler，当通过接口实例调用对应的suspend函数时候会触发invoke方法。

(2) 加载方法得到ServiceMethod的实例。如果缓存中有已加载好的ServiceMethod实例则直接返回。否则先解析对应的方法，得到ServiceMethod实例后，缓存起来以便以后重复利用。

(3)  根据方法得到返回值类型。由Retrofit配置的CallAdapter.Factory构建CallAdapter实例。构建SuspendForBody的实例

(4) 调用CallAdapter#adapt方法将自定义的Call和OkHttpCall关联起来。

(5) 由自定义的ApiResultCall调用enqueue函数，将请求交给OkHttpCall执行。执行完的结果会通过Callback的回调，并得到返回值

ApiResult或抛出异常。本例而言不会抛出异常。

(6) 被委托的OkHttpCall在执行异步网络请求过程中，先获取RealCall对象，然后调用enqueue函数执行网络请求。

这样通过自定义CallAdapter.Factory、CallAdapter、Call，并给Retrofit配置自定义的CallAdapter.Factory，可实现调用apiInterface的suspend方法得到自定义的结果，避免了使用回调来处理异步。

# 4 使用

基于Retrofit网络库使用Kotlin协程进一步封装后，就可以使用了，使用起来也比较方便，比如可以在ViewModel中通过viewModelScope启动一个协程，在协程体中获取apiInterface的实例并调用对应的suspend方法，根据方法的返回值来更新ViewModel中的LiveData字段，然后通知View。

```k
class LoginViewModel() : ViewModel() {
	...
    fun login2(account: String, pwd: String) {
        viewModelScope.launch {
            when (val userApiResult = HttpManager.service.login2(account, pwd)) {
                is ApiResult.Success -> {
                    val user = userApiResult.data.data
                    Log.d(TAG, "login2: name: ${user.username}")
                }

                is ApiResult.Failure -> {
                    Log.d(
                        TAG,
                        "login2: errorCode:${userApiResult.errorCode}, errorMsg:${userApiResult.errorMsg}"
                    )
                }
            }

        }
    }
    ...
}
```



