package com.yopeso.parkingclient

import android.app.ProgressDialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener

open class BaseActivity : AppCompatActivity(), OnConnectionFailedListener {
    protected lateinit var mGoogleApiClient: GoogleApiClient

    protected lateinit var mGoogleSignInOptions: GoogleSignInOptions

    val lazyProgressDialog: ProgressDialog by lazy {
        val progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
        progressDialog.setMessage("Loading ...")
        progressDialog
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, mGoogleSignInOptions)
            .build()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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

