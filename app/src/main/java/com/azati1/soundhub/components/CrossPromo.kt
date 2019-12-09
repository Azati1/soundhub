package com.azati1.soundhub.components

import com.google.gson.annotations.SerializedName

data class CrossPromo(
    @SerializedName("strategy")
    val strategy: String,
    @SerializedName("recomendedApps")
    val recomendedApps: List<RecomendedApp>
)

data class RecomendedApp(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("picture")
    val pictureUrl: String,
    @SerializedName("link")
    val appUrl: String
)