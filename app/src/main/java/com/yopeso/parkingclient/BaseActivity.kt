package com.yopeso.parkingclient

import android.app.ProgressDialog
import android.support.v7.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    val lazyProgressDialog: ProgressDialog by lazy {
        val progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading ...")
        progressDialog
    }

    fun showProgressDialog() {
        lazyProgressDialog.show()
    }

    fun hideProgressDialog() {
        if (lazyProgressDialog.isShowing) {
            lazyProgressDialog.dismiss()
        }
    }

}

