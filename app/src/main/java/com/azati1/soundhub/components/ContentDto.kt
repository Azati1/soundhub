package com.azati1.soundhub.components

import com.google.gson.annotations.SerializedName

data class ContentDto(
    @SerializedName("main_picture")
    val mainPicture: String,
    @SerializedName("content")
    val content: List<ContentItemDto>
)