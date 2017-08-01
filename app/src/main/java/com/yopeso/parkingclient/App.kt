package com.yopeso.parkingclient

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.facebook.drawee.backends.pipeline.Fresco
import io.fabric.sdk.android.Fabric


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Fresco.initialize(this)
        Fabric.with(this, Crashlytics())
    }
}