package com.azati1.soundhub.ui.main

import android.content.Context
import com.azati1.soundhub.components.AdsDto
import com.azati1.soundhub.components.AppComponent
import com.azati1.soundhub.components.ContentDto
import com.azati1.soundhub.components.CrossPromo
import com.azati1.soundhub.repo.Repository
import io.reactivex.Single

class MainModel {

    private var repository: Repository

    constructor(context: Context){
        repository = Repository(AppComponent.getOrCreateApiService(context))
    }

    fun getContent(url: String) : Single<ContentDto> {
        return repository.getContent(url)
    }

    fun getAds() : Single<AdsDto> {
        return repository.getAdsData()
    }

    fun getCrossPromo() : Single<CrossPromo> {
        return repository.getCrossPromo()
    }

}