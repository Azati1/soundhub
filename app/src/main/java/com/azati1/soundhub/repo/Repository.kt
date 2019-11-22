package com.azati1.soundhub.repo

import com.azati1.soundhub.components.ApiService
import com.azati1.soundhub.components.AdsDto
import com.azati1.soundhub.components.ContentDto
import io.reactivex.Single
import java.io.File

class Repository(
    private val apiService: ApiService
) {

    fun getFileByPath(path: String): File? {
        return null
    }

    fun getAdsData() : Single<AdsDto> {
        return apiService.getAdmobData()
    }

    fun getContent() : Single<ContentDto> {
        return apiService.getContentData()
    }

}