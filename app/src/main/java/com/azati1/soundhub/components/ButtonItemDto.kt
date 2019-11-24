package com.azati1.soundhub.components

import com.google.gson.annotations.SerializedName

data class ButtonItemDto(
    @SerializedName("name")
    val name: String,
    @SerializedName("sound")
    val sound: String,
    @SerializedName("picture")
    val picture: String
)
