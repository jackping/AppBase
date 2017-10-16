package com.yima.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yima.base.common.RxBusObj
import javax.inject.Inject

/**
 * BaseActivity
 * Created by yima on 2017/3/28.
 */
abstract class BaseActivity<T : IPresenter<IUiBehavior>> : SimpleActivity(), IUiBehavior {

    @Inject
    protected lateinit var mPresenter: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initComponent()
        mPresenter.attachView(this)
        initView(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }

    /**
     * init ui
     */
    protected abstract fun initView(bundle: Bundle?)

    /**
     * init di
     */
    protected abstract fun initComponent()
}


/**
 * BaseFragment
 * Created by yima on 2017/3/28.
 */
abstract class BaseFragment<T : IPresenter<IUiBehavior>> : SimpleFragment(), IUiBehavior {
    @Inject
    lateinit var mPresenter: T

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        initComponent()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mPresenter.attachView(this)
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }

    /**
     * init di
     */
    protected abstract fun initComponent()
}

/**
 * BaseRxActivity
 * Created by yima on 2017/4/11.
 */
abstract class BaseRxActivity<T : IPresenter<IUiBehavior>> : BaseActivity<T>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RxBusObj.get().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        RxBusObj.get().unregister(this)
    }
}

/**
 * BaseRxFragment
 * Created by yima on 2017/4/11.
 */
abstract class BaseRxFragment<T : IPresenter<IUiBehavior>> : BaseFragment<T>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RxBusObj.get().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        RxBusObj.get().unregister(this)
    }
}

/**
 * SimpleRxFragment
 * Created by yima on 2017/3/28.
 */
abstract class SimpleRxFragment : SimpleFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RxBusObj.get().register(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        RxBusObj.get().unregister(this)
    }
}