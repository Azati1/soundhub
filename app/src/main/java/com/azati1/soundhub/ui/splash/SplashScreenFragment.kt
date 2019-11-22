package com.azati1.soundhub.ui.splash

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils

import com.azati1.soundhub.R
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreenFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_splash_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startIconPulseAnimation()
    }

    private fun startIconPulseAnimation() {
        val pulseAnimation = AnimationUtils.loadAnimation(
            context,
            R.anim.icon_pulse_animation
        )
        application_icon.startAnimation(pulseAnimation)
    }

    companion object {

        fun create() : SplashScreenFragment {
            return SplashScreenFragment()
        }

    }

}
