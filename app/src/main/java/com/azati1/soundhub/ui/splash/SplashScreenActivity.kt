package com.azati1.soundhub.ui.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import com.azati1.soundhub.R
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        startIconPulseAnimation()
    }

    private fun startIconPulseAnimation() {
        val pulseAnimation = AnimationUtils.loadAnimation(this,
            R.anim.icon_pulse_animation
        )
        application_icon.startAnimation(pulseAnimation)
    }
}
