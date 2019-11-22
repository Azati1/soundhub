package com.azati1.soundhub.components

import java.io.Serializable

class Content(
    val mainPicture: String,
    val content: List<ContentItem>
) : Serializable