package com.yima.base

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * IUiBehavior
 * Created by yima on 2017/6/13.
 */
interface IUiBehavior {
    fun showError(msg: String)
}

/**
 * IPresenter
 * Created by yima on 2017/6/13.
 */
interface IPresenter<out T : IUiBehavior> {
    fun <V : IUiBehavior> attachView(view: V)

    fun detachView()
}

/**
 * RxPresenter
 * Created by yima on 2017/3/28.
 */

open class RxPresenter<T : IUiBehavior> : IPresenter<T> {
    protected var mView: T? = null
    protected val mCompositeDisposable: CompositeDisposable by lazy { CompositeDisposable() }

    override fun <V : IUiBehavior> attachView(view: V) {
        this.mView = view as T
    }

    override fun detachView() {
        this.mView = null
        dispose()
    }

    protected fun dispose() {
        mCompositeDisposable.dispose()
    }

    fun addDisposable(disposable: Disposable) {
        mCompositeDisposable.add(disposable)
    }
}