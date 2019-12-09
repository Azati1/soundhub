package com.azati1.soundhub.ui.main

import com.azati1.soundhub.components.AdsDto
import com.azati1.soundhub.components.AppComponent
import com.azati1.soundhub.components.ContentDto
import com.azati1.soundhub.components.CrossPromo
import com.azati1.soundhub.repo.Repository
import io.reactivex.Single

class MainModel {

    private val repository = Repository(AppComponent.getOrCreateApiService())

    fun getContent() : Single<ContentDto> {
        return repository.getContent()
    }

    fun getAds() : Single<AdsDto> {
        return repository.getAdsData()
    }

    fun getCrossPromo() : Single<CrossPromo> {
        return repository.getCrossPromo()
    }

}