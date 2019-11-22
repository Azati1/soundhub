package com.azati1.soundhub.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import android.view.Gravity
import android.view.Window
import com.azati1.soundhub.R
import com.azati1.soundhub.ui.splash.SplashScreenFragment

class MainActivity : AppCompatActivity(), MainFragment.MainFragmentListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initToolbar()
        initFragment()
    }

    private fun initFragment() {
        supportFragmentManager.beginTransaction().replace(
            R.id.container,
            MainFragment.create()
        ).commit()
    }

    private fun initToolbar() {
        toolbarButton.setOnClickListener {
            showMenu()
        }
    }

    private fun showMenu() {
        val menuItems = arrayOf<CharSequence>("Privacy Policy", "Personalized Ads", "More apps")
        val builder = AlertDialog.Builder(this)
        builder.setItems(menuItems) { dialog, item ->
            when (item) {
                0 -> Toast.makeText(this, "Privacy Policy", Toast.LENGTH_SHORT).show()
                1 -> Toast.makeText(this, "Personalized Ads", Toast.LENGTH_SHORT).show()
                2 -> Toast.makeText(this, "More apps", Toast.LENGTH_SHORT).show()
            }
        }
        val menu = builder.create()
        menu.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val layoutParams = menu.window?.attributes
        layoutParams?.let {
            layoutParams.gravity = Gravity.TOP or Gravity.START
        }
        menu.show()
    }

    override fun onDataLoaded() {
        //supportFragmentManager.popBackStack()
    }

}
