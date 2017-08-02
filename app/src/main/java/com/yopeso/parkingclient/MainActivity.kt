package com.yopeso.parkingclient

import android.os.Bundle
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.google.android.gms.auth.api.Auth
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class MainActivity : BaseActivity() {

    private lateinit var mStorageRef: StorageReference
    private lateinit var ivPhoto: SimpleDraweeView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mStorageRef = FirebaseStorage.getInstance().reference
        ivPhoto = findViewById(R.id.iv_current_photo) as SimpleDraweeView
        findViewById(R.id.fab_refresh).setOnClickListener {
            ivPhoto.setActualImageResource(0)
            syncImage()
        }
        findViewById(R.id.fab_logout).setOnClickListener {
            showProgressDialog()
            Auth.GoogleSignInApi.signOut(mGoogleApiClient)
            FirebaseAuth.getInstance().signOut()
            startActivity(SignInActivity.newIntent(this))
            finish()
        }
        syncImage()
    }

    fun syncImage(): Unit {
        mStorageRef.child("parking/omg.png").downloadUrl.addOnSuccessListener {

            val controller = Fresco.newDraweeControllerBuilder()
                    .setUri(it)
                    .setAutoPlayAnimations(true)
                    .build()

            ivPhoto.controller = controller

        }.addOnFailureListener {
            it.printStackTrace()
        }
    }
}