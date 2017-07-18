package com.yopeso.parkingclient

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var carImageView: ImageView
    private var handler: Handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        carImageView = findViewById(R.id.car_image_view) as ImageView

        Handler().postDelayed({
            startAnimation()
            delayStartActivity()
        }, 1500)
    }

    fun delayStartActivity() {
        handler.postDelayed(Runnable {
            startActivity(SignInActivity.newIntent(this))
            overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out)
            finish()
        }, 650)
    }

    fun startAnimation() {
        val slideAnimation: Animation = AnimationUtils.loadAnimation(applicationContext, R.anim.slide)
        carImageView.startAnimation(slideAnimation)

    }
}

