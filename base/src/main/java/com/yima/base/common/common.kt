package com.yima.base.common

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.engine.cache.ExternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import com.hwangjr.rxbus.Bus
import com.hwangjr.rxbus.thread.ThreadEnforcer
import com.yima.base.BaseApp
import timber.log.Timber
import java.io.InputStream
import java.io.PrintWriter
import java.io.StringWriter

/**
 * GlideMainModule
 * Created by yima on 2017/8/24.
 */
@GlideModule
class GlideMainModule : AppGlideModule() {
    override fun applyOptions(context: Context?, builder: GlideBuilder?) {
        if (builder == null || context == null) {
            return
        }
        val memorySize = 32 * 1024 * 1024
        builder.setMemoryCache(LruResourceCache(memorySize))

        val diskSize = 100 * 1024 * 1024
        builder.setDiskCache(ExternalCacheDiskCacheFactory(context, diskSize))
    }

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }

    override fun registerComponents(context: Context?, glide: Glide?, registry: Registry?) {
        if (registry == null) {
            return
        }
        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory())
    }
}


/**
 * ActivityLifecycleCallbackImpl
 * Created by yima on 2017/4/12.
 */
class ActivityLifecycleCallbackImpl : Application.ActivityLifecycleCallbacks {
    var count: Int = 0
        private set

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle) {
    }

    override fun onActivityStarted(activity: Activity) {
        count++
        Timber.d("onActivityStarted " + count + " " + activity.javaClass.name)
    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {
        count--
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
    }

    override fun onActivityDestroyed(activity: Activity) {

    }
}

/**
 * CrashHandler
 * Created by yima on 2017/3/29.
 */
class CrashHandler(arg: Context) : Thread.UncaughtExceptionHandler {

    private val context: Context = arg

    override fun uncaughtException(thread: Thread, ex: Throwable) {
        println(ex.toString())
        Timber.e(ex.toString())
        Timber.e(collectCrashDeviceInfo())
        Timber.e(getCrashInfo(ex))
        BaseApp.getApp().exitApp()
    }

    /**
     * 得到程序崩溃的详细信息
     */
    fun getCrashInfo(ex: Throwable): String {
        val result = StringWriter()
        val printWriter = PrintWriter(result)
        ex.printStackTrace(printWriter)
        return result.toString()
    }

    /**
     * 收集程序崩溃的设备信息
     */
    fun collectCrashDeviceInfo(): String {
        try {
            val pm = context.packageManager
            val pi = pm.getPackageInfo(context.packageName, PackageManager.GET_ACTIVITIES)
            val versionName = pi.versionName
            val model = android.os.Build.MODEL
            val androidVersion = android.os.Build.VERSION.RELEASE
            val manufacturer = android.os.Build.MANUFACTURER
            return "$versionName  $model  $androidVersion  $manufacturer"
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return ""
    }
}

/**
 * RxBus
 * Created by yima on 2017/6/13.
 */

object RxBusObj {
    private val mBus: Bus by lazy {
        Bus(ThreadEnforcer.ANY)
    }

    fun get() = mBus
}