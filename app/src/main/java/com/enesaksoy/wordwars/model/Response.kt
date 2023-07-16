package com.enesaksoy.wordwars.model

import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("items")
    val items: List<Item>,
    @SerializedName("text")
    val text: String
)