package com.enesaksoy.wordwars.model


import com.google.gson.annotations.SerializedName

data class Item(
    @SerializedName("item")
    val item: String,
    @SerializedName("pos")
    val pos: String,
    @SerializedName("weight")
    val weight: Int
)