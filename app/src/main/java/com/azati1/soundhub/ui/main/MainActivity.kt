package com.azati1.soundhub.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import android.view.Gravity
import android.view.Window
import com.azati1.soundhub.R
import com.azati1.soundhub.components.ContentDto
import com.azati1.soundhub.ui.splash.SplashScreenFragment
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity(), OnMainFragmentDataLoaded {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val model = MainModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initToolbar()
        initFragment()
        loadData()
    }

    private fun initFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.upperContainer,
                SplashScreenFragment.create(),
                "splash"
            )
            .commit()
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

    private fun loadData() {
        compositeDisposable.add(
            model.getAds()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe ({
                    Log.d("CDA123", it.admobAppId)
                }, {t ->
                    Log.d("CDA123", t.message)
                }))

        compositeDisposable.add(
            model.getContent()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe ({
                    onDataLoaded(it)
                }, {t ->
                    Log.d("CDA123", t.message)
                }))
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    override fun onImagesLoaded() {
        val fragement = supportFragmentManager.findFragmentByTag("splash")
        fragement?.let {
            supportFragmentManager
                .beginTransaction()
                .remove(fragement)
                .commit()
        }
    }


    private fun onDataLoaded(content: ContentDto) {


        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, MainFragment.create(content))
            .commit()

    }

}
