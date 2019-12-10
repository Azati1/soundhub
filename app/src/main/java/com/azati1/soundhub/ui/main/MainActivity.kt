package com.azati1.soundhub.ui.main

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.ConnectivityManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import android.view.Gravity
import android.view.Window
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.azati1.soundhub.R
import com.azati1.soundhub.RateAppDialogFragment
import com.azati1.soundhub.components.AdsDto
import com.azati1.soundhub.components.AppComponent
import com.azati1.soundhub.components.ContentDto
import com.azati1.soundhub.components.CrossPromo
import com.azati1.soundhub.ui.section.SECTION_CONTENT_FRAGMENT
import com.azati1.soundhub.ui.splash.SplashScreenFragment
import com.downloader.OnDownloadListener
import com.downloader.PRDownloader
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardedVideoAd
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.io.File
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

const val PREFS_NAME: String = "PREFERENCES"
private const val ACCEPTED: String = "ACCEPTED"
 const val IS_MARKET_PAGE_SHOWED = "IS_MARKET_PAGE_SHOWED"

class MainActivity : AppCompatActivity(), OnSoundAction, SectionRecyclerViewEvents, OnPageShow, OnRateAppAction {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private lateinit var model: MainModel
    private lateinit var dirPath: String
    private var player: MediaPlayer = MediaPlayer()
    private var pageShowCounter = 0

    private var interstitialAd: InterstitialAd? = null

    private val rateAppSubject = PublishSubject.create<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.activity_main)
        initToolbar()
        initFragment()
        model = MainModel(this)
        dirPath = applicationInfo.dataDir
        loadData()
        //printKeyHash()
    }

    private fun showPrivacyAlert(url: String, title: String) {
        lateinit var dialog: AlertDialog

        var webView = WebView(applicationContext)
        webView.loadUrl(url)
        webView.settings.javaScriptEnabled = true
        webView.settings.userAgentString = "Mozilla/5.0 (iPhone; U; CPU like Mac OS X; en) AppleWebKit/420+ (KHTML, like Gecko) Version/3.0 Mobile/1A543a Safari/419.3"
        webView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d("MSG", "PAGE LOAD FINISHED")
            }

        }
        webView.webChromeClient = WebChromeClient()

        var builder = AlertDialog.Builder(this)
        builder
            .setTitle(title)
            .setView(webView)

        dialog = builder.create()
        dialog.show()
    }

    private fun showFirstPrivacyAlert() {
        if (!getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getBoolean(ACCEPTED, false)) {


            lateinit var dialog: AlertDialog

            var builder = AlertDialog.Builder(this)
            builder
                .setTitle(getString(R.string.attention))
                .setMessage(getString(R.string.first_dialog_text))
                .setCancelable(false)
                .setPositiveButton(R.string.accept, null)
                .setNegativeButton(R.string.decline, null)

            dialog = builder.create()
            dialog.show()

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().putBoolean(ACCEPTED, true).commit()
                dialog.dismiss()
            }
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                finish()
            }

        }
    }

    private fun initAds() {

        val adsDto = (applicationContext as AppComponent).getAdsDto()

        adsDto?.let {
            MobileAds.initialize(this, adsDto.admobAppId)

            val adRequest = AdRequest.Builder().build()
            interstitialAd = InterstitialAd(this)

            interstitialAd?.adUnitId = adsDto.admobInterstitialId
            interstitialAd?.adListener = object: AdListener() {
                override fun onAdClosed() {
                    interstitialAd?.loadAd(AdRequest.Builder().build())
                }
            }
            interstitialAd?.loadAd(adRequest)
        }
    }

    private fun displayInterstitial() {
        interstitialAd?.let {
            if (it.isLoaded)
                it.show()
        }
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
        adsButton.setOnClickListener {
            displayInterstitial()
        }
    }

    private fun showMenu() {
        val menuItems = arrayOf<CharSequence>("Privacy Policy", "Personalized Ads", "More Apps")
        val builder = AlertDialog.Builder(this)
        builder.setItems(menuItems) { dialog, item ->
            when (item) {
                0 -> showPrivacyAlert((applicationContext as AppComponent).getAdsDto()!!.privacyPolicyUrl, "Privacy Policy")
                1 -> showPrivacyAlert((applicationContext as AppComponent).getAdsDto()!!.gdprPolicyUrl, "Personalized Ads")
                2 -> startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=5584164941825017957")))
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
            model.getCrossPromo(),
            Function3 { t1: AdsDto, t2: ContentDto, t3: CrossPromo ->
                Log.d("SAS", "ZIP")
                (applicationContext as AppComponent).setAdsDto(t1)
                (applicationContext as AppComponent).setCrossPromo(t3)
                onDataLoaded(t2)

            }).observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .doOnError {
                retriesCount++
                if (retriesCount == 5) {
                    Toast.makeText(this, getString(R.string.connection_error), Toast.LENGTH_LONG).show()
                    retriesCount = 0
                }
            }
            .retryWhen { errors ->
                Log.d("CDA123", "retryWhen")
                errors.delay(2, TimeUnit.SECONDS)
            }
            .subscribe({ res ->
                Log.d("SAS", "SUCC")
                initAds()
                initRateDialog()
                showFirstPrivacyAlert()

                if(isOnline(applicationContext))
                    cacheWebView.clearCache(true)

                cacheWebView.loadUrl((applicationContext as AppComponent).getAdsDto()!!.privacyPolicyUrl)
                cacheWebView.settings.javaScriptEnabled = true
                cacheWebView.settings.userAgentString = "Mozilla/5.0 (iPhone; U; CPU like Mac OS X; en) AppleWebKit/420+ (KHTML, like Gecko) Version/3.0 Mobile/1A543a Safari/419.3"
                cacheWebView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                cacheWebView.webViewClient = WebViewClient()
                cacheWebView.webChromeClient = WebChromeClient()


                cacheWebViewGdpr.loadUrl((applicationContext as AppComponent).getAdsDto()!!.gdprPolicyUrl)
                cacheWebViewGdpr.settings.javaScriptEnabled = true
                cacheWebViewGdpr.settings.userAgentString = "Mozilla/5.0 (iPhone; U; CPU like Mac OS X; en) AppleWebKit/420+ (KHTML, like Gecko) Version/3.0 Mobile/1A543a Safari/419.3"
                cacheWebViewGdpr.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
                cacheWebViewGdpr.webViewClient = WebViewClient()
                cacheWebViewGdpr.webChromeClient = WebChromeClient()



            }, { err ->
                Log.d("SAS", "ERR")
            })
        )

    }

    fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun initRateDialog() {

        val isMarketPageShowed = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(IS_MARKET_PAGE_SHOWED, false)

        if (!isMarketPageShowed) {
            val adsDto = (applicationContext as AppComponent).getAdsDto()
            adsDto?.let { adsDto ->
                rateAppSubject
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .delay(adsDto.rateDialogFrequency.toLong(), TimeUnit.SECONDS)
                    .subscribe ({
                        val isRateAppDialogShowed = (applicationContext as? AppComponent)?.isRateUsDialogShowed ?: false
                        if (!isRateAppDialogShowed) {
                            Log.d("CDA123", "show rate dialog from main")
                            (applicationContext as? AppComponent)?.isRateUsDialogShowed = true
                            RateAppDialogFragment.create().show(supportFragmentManager, "")
                        }
                    }, { err -> Log.d("CDA", err.message)})
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
        rateAppSubject.onComplete()
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

    }

    override fun onPageShowed() {
        pageShowCounter++

        val adsDto = (applicationContext as AppComponent).getAdsDto()

        adsDto?.let {
            if (pageShowCounter > 0 && pageShowCounter % adsDto.interstitialFrequency == 0) {
                displayInterstitial()
            }
        }
    }

    override fun onClickRateButton() {
        rateAppSubject.onComplete()
        (applicationContext as? AppComponent)?.isRateUsDialogShowed = false
        getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(IS_MARKET_PAGE_SHOWED, true)
            .apply()
    }

    override fun onClickDismissButton() {
        rateAppSubject.onNext(false)
        (applicationContext as? AppComponent)?.isRateUsDialogShowed = false

        val isMarketPageShowed =
            getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getBoolean(IS_MARKET_PAGE_SHOWED, false)

        if (!isMarketPageShowed) {
            getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putBoolean(IS_MARKET_PAGE_SHOWED, false)
                .apply()
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

interface OnRateAppAction {
    fun onClickRateButton()
    fun onClickDismissButton()
}