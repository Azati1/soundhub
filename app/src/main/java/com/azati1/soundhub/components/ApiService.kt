package com.azati1.soundhub.components

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {

    @GET
    fun getContentData(@Url url: String): Single<ContentDto>

    @GET("https://newsoundboard.xyz/admob.php") // адрес файла с настройками рекламы
    fun getAdmobData(): Single<AdsDto>

    @GET("https://newsoundboard.xyz/crosspromo.php") // адрес файла с настройками рекомендуемых приложений
    fun getCrossPromo(): Single<CrossPromo>

}