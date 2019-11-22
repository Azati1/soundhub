package com.azati1.soundhub.components

import java.io.Serializable

class ContentItem(
    val name: String,
    val pictureUrl: String,
    val buttons: List<ButtonItem>
) : Serializable