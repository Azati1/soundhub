package com.azati1.soundhub.components

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Url

interface ApiService {

    @GET
    fun getContentData(@Url url: String): Single<ContentDto>

    @GET // адрес файла с настройками рекламы
    fun getAdmobData(@Url url: String): Single<AdsDto>

    @GET // адрес файла с настройками рекомендуемых приложений
    fun getCrossPromo(@Url url: String): Single<CrossPromo>

}