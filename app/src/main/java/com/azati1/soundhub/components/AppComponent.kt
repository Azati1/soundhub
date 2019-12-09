package com.azati1.soundhub.components

import android.app.Application
import com.azati1.soundhub.R
import com.downloader.PRDownloader
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig

class AppComponent : Application() {

    private var ads: AdsDto? = null
    private var crossPromo: CrossPromo? = null
    var isRateUsDialogShowed = false

    companion object {

        var apiService: ApiService? = null

        fun getOrCreateApiService() : ApiService {
            if (apiService == null) {

                val okHttpClient = OkHttpClient.Builder()
                    .build()


                val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .baseUrl("https://newsoundboard.xyz/")
                    .build()
                apiService = retrofit.create(ApiService::class.java)
            }
            return apiService!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        PRDownloader.initialize(applicationContext)
        /*val config = YandexMetricaConfig.newConfigBuilder(resources.getString(R.string.appMetricaKey)).build()
        YandexMetrica.activate(applicationContext, config)
        YandexMetrica.enableActivityAutoTracking(this)*/
    }

    fun setAdsDto(adsDto: AdsDto) {
        ads = adsDto
    }

    fun getAdsDto(): AdsDto? {
        return ads
    }

    fun setCrossPromo(crossPromo: CrossPromo) {
        this.crossPromo = crossPromo
    }

    fun getCrossPromo(): CrossPromo? {
        return crossPromo
    }

}