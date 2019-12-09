package com.azati1.soundhub.components

import io.reactivex.Single
import retrofit2.http.GET

interface ApiService {

    @GET("json.php")
    fun getContentData(): Single<ContentDto>

    @GET("admob.php")
    fun getAdmobData(): Single<AdsDto>

    @GET("crosspromo.php")
    fun getCrossPromo(): Single<CrossPromo>

}