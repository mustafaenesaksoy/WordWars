package com.enesaksoy.wordwars.model


import com.google.gson.annotations.SerializedName

data class Request(
    @SerializedName("indent")
    val indent: String,
    @SerializedName("lang")
    val lang: String,
    @SerializedName("limit")
    val limit: Int,
    @SerializedName("pos")
    val pos: String,
    @SerializedName("text")
    val text: List<String>,
    @SerializedName("type")
    val type: String
)