package com.azati1.soundhub.repo

import com.azati1.soundhub.components.ApiService
import com.azati1.soundhub.components.AdsDto
import com.azati1.soundhub.components.ContentDto
import com.azati1.soundhub.components.CrossPromo
import io.reactivex.Single

class Repository(
    private val apiService: ApiService
) {

    fun getAdsData(url: String) : Single<AdsDto> {
        return apiService.getAdmobData(url)
    }

    fun getContent(url: String) : Single<ContentDto> {
        return apiService.getContentData(url)
    }

    fun getCrossPromo(url: String) : Single<CrossPromo> {
        return apiService.getCrossPromo(url)
    }

}