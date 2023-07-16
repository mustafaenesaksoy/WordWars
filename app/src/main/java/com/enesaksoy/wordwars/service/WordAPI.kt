package com.enesaksoy.wordwars.service

import com.enesaksoy.wordwars.model.WordResult
import retrofit2.Response
import retrofit2.http.GET

interface WordAPI {

    @GET("word?lang=en")
    suspend fun getWord(): Response<WordResult>
}