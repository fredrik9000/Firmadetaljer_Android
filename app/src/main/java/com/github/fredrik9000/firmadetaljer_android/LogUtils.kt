package com.github.fredrik9000.firmadetaljer_android

import android.util.Log

object LogUtils {
    @JvmStatic
    fun debug(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message)
        }
    }
}