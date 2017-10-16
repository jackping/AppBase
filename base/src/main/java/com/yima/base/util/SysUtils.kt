package com.yima.base.util

import android.content.Context
import android.net.ConnectivityManager
import com.yima.base.BaseApp

/**
 * SysUtil
 * Created by yima on 2017/6/13.
 */
object SysUtil {
    fun hasInternet(): Boolean {
        val cm = BaseApp.getApp().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = cm.activeNetworkInfo
        return info != null
    }

    fun hasWifi(): Boolean {
        val cm = BaseApp.getApp().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = cm.activeNetworkInfo
        if (info == null || !info.isConnected) {
            return false
        }
        return ConnectivityManager.TYPE_WIFI == info.type
    }

    fun getVersionCode() = BaseApp.getApp().packageManager.getPackageInfo(BaseApp.getApp().packageName, 0).versionName
}