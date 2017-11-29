package com.yima.base

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.yokeyword.fragmentation.SupportActivity
import me.yokeyword.fragmentation.SupportFragment

/**
 * SimpleActivity
 * Created by yima on 2017/3/28.
 */

abstract class SimpleActivity : SupportActivity() {
    protected lateinit var mContext: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inflateView()
        mContext = this
        BaseApp.getApp().addActivity(this)
    }

    protected fun initToolBar(toolbar: Toolbar?) {
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        BaseApp.getApp().removeActivity(this)
    }

    protected abstract val layoutId: Int

    protected open fun inflateView() {
        setContentView(layoutId)
    }
}

/**
 * SimpleFragment
 * Created by yima on 2017/3/28.
 */

abstract class SimpleFragment : SupportFragment() {

    protected lateinit var mView: View
    protected lateinit var mActivity: Activity
    protected lateinit var mContext: Context
    private var isInit = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mActivity = activity
        mContext = context
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mActivity = context as Activity
        mContext = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = inflater.inflate(layoutId, null)
        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!isHidden) {
            isInit = true
            initView()
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!isInit && !hidden) {
            isInit = true
            initView()
        }
    }

    open fun onKeyDown(keyCode: Int, event: KeyEvent?) = true

    protected abstract val layoutId: Int

    protected abstract fun initView()
}
