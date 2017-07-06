package com.yopeso.parkingclient

import android.os.Bundle
import android.os.PersistableBundle

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_main)
    }
}