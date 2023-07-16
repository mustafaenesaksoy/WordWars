package com.enesaksoy.wordwars.model


import com.google.gson.annotations.SerializedName

data class KeyResult(
    @SerializedName("code")
    val code: Int,
    @SerializedName("request")
    val request: Request,
    @SerializedName("response")
    val response: List<Response>,
    @SerializedName("version")
    val version: String
)