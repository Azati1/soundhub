package com.azati1.soundhub.components

import com.google.gson.annotations.SerializedName

data class ContentItemDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("picture")
    val pictureUrl: String,
    @SerializedName("buttons")
    val buttons: List<ButtonItemDto>
)