package com.azati1.soundhub.ui.main

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import android.view.Gravity
import android.view.Window
import androidx.core.content.FileProvider
import com.azati1.soundhub.R

import com.azati1.soundhub.components.ContentDto
import com.azati1.soundhub.ui.splash.SplashScreenFragment
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.utils.Utils
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import java.io.File


class MainActivity : AppCompatActivity(), OnMainFragmentDataLoaded {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val model = MainModel()
    private lateinit var dirPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dirPath = applicationInfo.dataDir

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

    private fun showMainFragment(content: ContentDto){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, MainFragment.create(content))
            .commit()
    }

    private fun onDataLoaded(content: ContentDto) {

        var requestsCount = 0


        content.content.forEach{itemsList ->
            itemsList.buttons.forEach{button ->

                val fileName = Uri.parse(button.sound).lastPathSegment
                val url = button.sound

                if(!File("$dirPath/$fileName").exists()){
                    requestsCount++
                    Log.d("FILE_DOWNLOAD", "DOWNLOAD REQUEST $requestsCount")
                    File("$dirPath/$fileName").createNewFile()
                    PRDownloader.download(url, dirPath, fileName)
                        .build()
                        .setOnStartOrResumeListener { }
                        .setOnPauseListener { }
                        .setOnCancelListener {

                        }
                        .setOnProgressListener{progress ->

                        }
                        .start(object : OnDownloadListener {
                            override fun onError(error: com.downloader.Error?) {
                            }

                            override fun onDownloadComplete() {
                                requestsCount--
                                Log.d("FILE_DOWNLOAD", "FILE DOWNLOAD IS COMPLETE")
                                if(requestsCount == 0){
                                    showMainFragment(content)
                                }
                            }

                        })
                } else {
                    Log.d("FILE_DOWNLOAD", "FILE EXISTS")
                }
            }
        }


        if(requestsCount == 0) {
            showMainFragment(content)
        }


/*

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, MainFragment.create(content))
            .commit()

 */

    }

}
