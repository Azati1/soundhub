package com.azati1.soundhub.ui.main

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import android.view.Gravity
import android.view.Window
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.azati1.soundhub.R
import com.azati1.soundhub.components.AdsDto
import com.azati1.soundhub.components.AppComponent
import com.azati1.soundhub.components.ContentDto
import com.azati1.soundhub.ui.section.SECTION_CONTENT_FRAGMENT
import com.azati1.soundhub.ui.splash.SplashScreenFragment
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.downloader.utils.Utils
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
private const val PREFS_NAME: String = "PREFERENCES"
private const val ACCEPTED: String = "ACCEPTED"
class MainActivity : AppCompatActivity(), OnSoundAction, SectionRecyclerViewEvents, OnPageShow {




    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val model = MainModel()
    private lateinit var dirPath: String
    private var player: MediaPlayer = MediaPlayer()
    private var pageShowCounter = 0

    private lateinit var interstitialAd: InterstitialAd




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dirPath = applicationInfo.dataDir

        setContentView(R.layout.activity_main)
        initToolbar()
        initFragment()
        loadData()
        initAds()



    }

    private fun showPrivacyAlert(url: String, cancellable: Boolean){
        if(!getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getBoolean(ACCEPTED, false)){
            lateinit var dialog: AlertDialog
            var webView = WebView(applicationContext)
            webView.loadUrl(url)

            webView.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    if(!cancellable)
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = true
                    Log.d("MSG", "PAGE LOAD FINISHED")
                }

            }
            webView.webChromeClient = WebChromeClient()


            var builder = AlertDialog.Builder(this)
            builder
                .setTitle(R.string.privacy_policy)
                .setView(webView)
                .setCancelable(cancellable)

            if(!cancellable){
                builder.setPositiveButton(R.string.accept, null)
                    .setNegativeButton(R.string.decline, null)
            }
            dialog = builder.create()
            dialog.show()
            if(!cancellable){
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = false
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                    getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putBoolean(ACCEPTED, true).commit()
                    dialog.dismiss()
                }
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                    finish()
                }
            }


        }
    }

    private fun initAds() {
        MobileAds.initialize(this)

        val adRequest = AdRequest.Builder().build()
        interstitialAd = InterstitialAd(this)

        interstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712"
        interstitialAd.adListener = object: AdListener() {
            override fun onAdClosed() {
                interstitialAd.loadAd(AdRequest.Builder().build())
            }
        }
        interstitialAd.loadAd(adRequest)
    }

    private fun displayInterstitial() {
        if (interstitialAd.isLoaded)
            interstitialAd.show()
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

        var retriesCount: Int = 0

        compositeDisposable.add(Single.zip(
            model.getAds(),
            model.getContent(),
            BiFunction { t1: AdsDto, t2: ContentDto ->
                Log.d("SAS", "ZIP")
                (applicationContext as AppComponent).setAdsDto(t1)

                onDataLoaded(t2)



            }).observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnError {
                retriesCount++
                if (retriesCount == 5) {
                    Toast.makeText(this, "Connection error", Toast.LENGTH_LONG).show()
                    retriesCount = 0
                }
            }
            .retryWhen { errors ->
                Log.d("CDA123", "retryWhen")
                errors.delay(2, TimeUnit.SECONDS)
            }
            .subscribe({ res ->
                Log.d("SAS", "SUCC")
                showPrivacyAlert((applicationContext as AppComponent).getAdsDto()!!.privacyPolicyUrl, false)
            }, { err ->
                Log.d("SAS", "ERR")
            })
        )


    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }


    private fun showMainFragment(content: ContentDto) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, MainFragment.create(content))
            .commit()
    }

    private fun onDataLoaded(content: ContentDto) {




        var requestsCount = 0

        var loading : ArrayList<String> = arrayListOf()


        content.content.forEach { itemsList ->
            itemsList.buttons.forEach { button ->

                val fileName = Uri.parse(button.sound).lastPathSegment
                val url = button.sound

                if (!loading.contains(fileName) && !File("$dirPath/$fileName").exists()) {
                    requestsCount++
                    Log.d("FILE_DOWNLOAD", "DOWNLOAD REQUEST $requestsCount")
                    loading.add(fileName!!)
                    //File("$dirPath/$fileName").createNewFile()
                    PRDownloader.download(url, dirPath, fileName)
                        .build()
                        .setOnStartOrResumeListener { }
                        .setOnPauseListener { }
                        .setOnCancelListener {

                        }
                        .setOnProgressListener { progress ->

                        }
                        .start(object : OnDownloadListener {
                            override fun onError(error: com.downloader.Error?) {
                            }

                            override fun onDownloadComplete() {
                                requestsCount--
                                Log.d("FILE_DOWNLOAD", "FILE DOWNLOAD IS COMPLETE")
                                if (requestsCount == 0) {

                                    showMainFragment(content)
                                }
                            }
                        })
                } else {
                    Log.d("FILE_DOWNLOAD", "FILE EXISTS")
                }
            }
        }


        if (requestsCount == 0) {

            showMainFragment(content)

        }

    }

    override fun onBackPressed() {
        val fragment =
            this.supportFragmentManager.findFragmentByTag(SECTION_CONTENT_FRAGMENT)


        (fragment as? OnBackPressed)?.let{
            if(it.onBackPressed()){
                super.onBackPressed()
            }
            return
        }

        super.onBackPressed()
    }

    override fun onSoundStarted(path: String) {

        player.reset()

        player.setDataSource(this, Uri.parse(path))
        player.setOnPreparedListener {
            player.start()
        }
        player.prepare()

    }

    override fun onSoundStopped() {
        player.stop()
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

    override fun onItemSelected() {
        //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPageShowed() {
        pageShowCounter++
        if (pageShowCounter > 0 && pageShowCounter % 5 == 0) {
            displayInterstitial()
        }
    }

}

interface OnSoundAction {
    fun onSoundStarted(path: String)
    fun onSoundStopped()
}

interface OnBackPressed {
    fun onBackPressed() : Boolean
}

interface OnPageShow {
    fun onPageShowed()
}

