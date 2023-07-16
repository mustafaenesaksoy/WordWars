package com.enesaksoy.wordwars.service

import com.enesaksoy.wordwars.model.KeyResult
import com.enesaksoy.wordwars.util.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface KeyAPI {

    @GET("associations/v1.0/json/search?")
    suspend fun getWordList(
        @Query("apikey") apiKey: String = API_KEY,
        @Query("text") text : String,
        @Query("lang") lang : String = "en"
    ):Response<KeyResult>
}