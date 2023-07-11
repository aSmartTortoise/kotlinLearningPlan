package com.kotlin.wanandroid.base

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus

abstract class BasePresenter<M: IModel, V: IView> : IPresenter<V>, LifecycleObserver {
    protected var model: M? = null
    protected var view: V? = null
    private val mIsViewAttached: Boolean
        get() = view != null
    private var mCompositeDisposable: CompositeDisposable? = null

    open fun createModel(): M? = null

    open fun useEventBus(): Boolean = false

    override fun attachView(view: V) {
        this.view = view
        this.model = createModel()
        if (view is LifecycleOwner) {
            (view as LifecycleOwner).lifecycle.addObserver(this)
            if (model != null && model is LifecycleObserver) {
                (view as LifecycleOwner).lifecycle.addObserver((model as LifecycleObserver))
            }
        }

        if (useEventBus()) {
            EventBus.getDefault().register(this)
        }
    }

    override fun detachView() {
        if (useEventBus()) {
            EventBus.getDefault().unregister(this)
        }

        unDispose()
        model?.onDetach()
        this.model = null
        this.view = null
    }

    private fun unDispose() {
        mCompositeDisposable?.clear()
        mCompositeDisposable = null
    }

    open fun checkViewAttached() {
        if (!mIsViewAttached) throw MvpViewNotAttachedException()
    }

    open fun addDisposable(disposable: Disposable?) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }

        disposable?.let { mCompositeDisposable?.add(it) }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(owner: LifecycleOwner) {
        owner.lifecycle.removeObserver(this)
    }

    private class MvpViewNotAttachedException internal constructor() : RuntimeException("Please call IPresenter.attachView(IBaseView) before" + " requesting data to the IPresenter")


}