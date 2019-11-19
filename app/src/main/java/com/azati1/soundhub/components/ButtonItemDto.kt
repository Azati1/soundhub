package com.azati1.soundhub.components

import com.google.gson.annotations.SerializedName

data class ButtonItemDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("cost")
    val cost: Int,
    @SerializedName("page")
    val pageUrl: String
)
