package com.igorunderplayer.unreminder

import android.app.Application
import com.onesignal.OneSignal

const val ONESIGNAL_APP_ID = "31e3478d-6958-43b9-a468-79e99962ddcc"

class ApplicationClass : Application() {
    override fun onCreate() {
        super.onCreate()

        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)

        // OneSignal Initialization
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)
    }
}