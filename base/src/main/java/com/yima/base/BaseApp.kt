package com.yima.base

import android.app.Activity
import android.app.Application
import android.app.Instrumentation
import android.content.Context
import android.os.Bundle
import android.os.PersistableBundle
import android.support.multidex.MultiDex
import android.util.DisplayMetrics
import android.util.SparseArray
import android.view.WindowManager
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.lang.ref.WeakReference


/**
 * com.yima.base.BaseApp
 * Created by yima on 2017/6/13.
 */
abstract class BaseApp : Application() {
    private val actManager = BaseActManager()
    var SCREEN_WIDTH = -1
    var SCREEN_HEIGHT = -1
    var DIMEN_RATE = -1.0F
    var DIMEN_DPI = -1

    init {
        //Timber
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        } else {
            //TODO 应该有一个release版只保存crashLog的tree
            Timber.plant(Timber.DebugTree())
        }
    }

    companion object {
        private lateinit var instance: BaseApp
        fun getApp() = instance
    }

    override fun onCreate() {
        avoidKotlinNullBundleError()
        super.onCreate()
        instance = this
        getScreenSize()
        initApp()
        //在子线程中初始化
        Observable.just("").subscribeOn(Schedulers.io()).subscribe { initAppAsync() }
    }

    protected abstract fun initApp()

    protected abstract fun initAppAsync()

    fun getScreenSize() {
        val windowManager = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val dm = DisplayMetrics()
        val display = windowManager.defaultDisplay
        display.getMetrics(dm)
        DIMEN_RATE = dm.density / 1.0f
        DIMEN_DPI = dm.densityDpi
        SCREEN_WIDTH = dm.widthPixels
        SCREEN_HEIGHT = dm.heightPixels
        if (SCREEN_WIDTH > SCREEN_HEIGHT) {
            val t = SCREEN_HEIGHT
            SCREEN_HEIGHT = SCREEN_WIDTH
            SCREEN_WIDTH = t
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    fun addActivity(act: Activity) {
        actManager.addActivity(act)
    }

    fun removeActivity(act: Activity) {
        actManager.removeActivity(act)
    }

    fun getTopActivity(): Activity? {
        return actManager.getTopActivity()
    }

    fun clearActivity() {
        actManager.clearActivity()
    }

    fun exitApp() {
        clearActivity()
        android.os.Process.killProcess(android.os.Process.myPid())
        System.exit(0)
    }

    private fun avoidKotlinNullBundleError() {
        try {
            val clz = Class.forName("android.app.ActivityThread")
            val mtd = clz.getDeclaredMethod("currentActivityThread")
            val obj = mtd.invoke(null)
            val field = obj.javaClass.getDeclaredField("mInstrumentation")
            field.isAccessible = true
            field.set(obj, InstrumentationBase())
        } catch (e: Exception) {
            Timber.e(e.message)
        }
    }
}

/**
 * com.yima.base.InstrumentationBase
 * add empty bundle to every activity
 * make sure no kotlin non-null argument error
 * Created by yima on 2017/10/16.
 */
class InstrumentationBase : Instrumentation() {
    override fun callActivityOnCreate(activity: Activity?, icicle: Bundle?) {
        var bundle = icicle
        if (bundle == null) {
            bundle = Bundle.EMPTY
        }
        super.callActivityOnCreate(activity, bundle)
    }

    override fun callActivityOnCreate(activity: Activity?, icicle: Bundle?, persistentState: PersistableBundle?) {
        var bundle = icicle
        if (bundle == null) {
            bundle = Bundle.EMPTY
        }
        super.callActivityOnCreate(activity, bundle, persistentState)
    }
}

class BaseActManager {
    private val container = SparseArray<WeakReference<Activity>>()
    private var topAct: WeakReference<Activity>? = null


    fun addActivity(act: Activity) {
        if (container.get(act.hashCode()) == null) {
            container.append(act.hashCode(), WeakReference(act))
            topAct = WeakReference(act)
        }
    }

    fun removeActivity(act: Activity) {
        val tmpAct = container.get(act.hashCode())
        if (tmpAct != null) {
            container.remove(act.hashCode())
            topAct = if (container.size() == 0) {
                null
            } else {
                container.get(container.keyAt(container.size() - 1))
            }
        }
    }

    fun getTopActivity(): Activity? {
        if (container.size() <= 0) {
            return null
        }
        return topAct?.get()
    }

    fun clearActivity() {
        synchronized(container) {
            var i = 0
            while (i < container.size()) {
                val key = container.keyAt(i)
                val weak = container.get(key)
                if (weak?.get() != null) {
                    weak.get()!!.finishAffinity()
                }
                container.remove(key)
                i++
            }
            topAct = null
        }
    }
}