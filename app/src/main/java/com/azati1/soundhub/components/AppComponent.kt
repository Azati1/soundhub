package com.azati1.soundhub.components

import android.app.Application
import com.azati1.soundhub.R
import android.content.Context
import com.downloader.PRDownloader
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.net.ConnectivityManager
import android.net.NetworkInfo
import okhttp3.Cache
import java.util.concurrent.TimeUnit


class AppComponent : Application() {

    private var ads: AdsDto? = null
    private var crossPromo: CrossPromo? = null
    var isRateUsDialogShowed = false

    companion object {

        var apiService: ApiService? = null
        val cacheSize = (5 * 1024 * 1024).toLong()

        fun getOrCreateApiService(context: Context) : ApiService {
            if (apiService == null) {

                val myCache = Cache(context.cacheDir, cacheSize)

                val okHttpClient = OkHttpClient.Builder()
                    .cache(myCache)
                    .addInterceptor { chain ->

                        // Get the request from the chain.
                        var request = chain.request()

                        /*
                        *  Leveraging the advantage of using Kotlin,
                        *  we initialize the request and change its header depending on whether
                        *  the device is connected to Internet or not.
                        */
                        request = if (hasNetwork(context)!!)
                        /*
                        *  If there is Internet, get the cache that was stored 5 seconds ago.
                        *  If the cache is older than 5 seconds, then discard it,
                        *  and indicate an error in fetching the response.
                        *  The 'max-age' attribute is responsible for this behavior.
                        */
                            request.newBuilder().header("Cache-Control", "public, max-age=" + 5).build()
                        else
                        /*
                        *  If there is no Internet, get the cache that was stored 7 days ago.
                        *  If the cache is older than 7 days, then discard it,
                        *  and indicate an error in fetching the response.
                        *  The 'max-stale' attribute is responsible for this behavior.
                        *  The 'only-if-cached' attribute indicates to not retrieve new data; fetch the cache only instead.
                        */
                            request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24).build()
                        // End of if-else statement

                        // Add the modified request to the chain.
                        chain.proceed(request)
                    }
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

        fun hasNetwork(context: Context): Boolean? {
            var isConnected: Boolean? = false // Initial Value
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
            if (activeNetwork != null && activeNetwork.isConnected)
                isConnected = true
            return isConnected
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