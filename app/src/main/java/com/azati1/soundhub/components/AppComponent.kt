package com.azati1.soundhub.components

import android.app.Application
import com.downloader.PRDownloader
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.xml.datatype.DatatypeConstants.SECONDS
import okhttp3.OkHttpClient
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import java.util.concurrent.TimeUnit


class AppComponent : Application() {

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
    }
}