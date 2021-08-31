package com.kotlin.wanandroid.base

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus

abstract class BasePresenter<M: IModel, V: IView> : IPresenter<V>, LifecycleObserver {
    protected var mModel: M? = null
    protected var mView: V? = null
    private val mIsViewAttached: Boolean
        get() = mView != null
    private var mCompositeDisposable: CompositeDisposable? = null

    abstract fun createModel(): M?

    open fun useEventBus(): Boolean = false

    override fun attachView(mView: V) {
        this.mView = mView
        this.mModel = createModel()
        if (mView is LifecycleOwner) {
            (mView as LifecycleOwner).lifecycle.addObserver(this)
            if (mModel != null && mModel is LifecycleObserver) {
                (mView as LifecycleOwner).lifecycle.addObserver((mModel as LifecycleObserver))
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
        mModel?.onDetach()
        this.mModel = null
        this.mView = null
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