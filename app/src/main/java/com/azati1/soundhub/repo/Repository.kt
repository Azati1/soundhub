package com.azati1.soundhub.repo

import com.azati1.soundhub.components.ApiService
import com.azati1.soundhub.components.AdsDto
import com.azati1.soundhub.components.ContentDto
import com.azati1.soundhub.components.CrossPromo
import io.reactivex.Single

class Repository(
    private val apiService: ApiService
) {

    fun getAdsData() : Single<AdsDto> {
        return apiService.getAdmobData()
    }

    fun getContent() : Single<ContentDto> {
        return apiService.getContentData()
    }

    fun getCrossPromo() : Single<CrossPromo> {
        return apiService.getCrossPromo()
    }

}