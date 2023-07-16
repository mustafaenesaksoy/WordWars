package com.enesaksoy.wordwars.repo

import com.enesaksoy.wordwars.model.KeyResult
import com.enesaksoy.wordwars.model.WordResult
import com.enesaksoy.wordwars.util.Resource
import com.google.firebase.auth.AuthResult

interface WordRepository {

    suspend fun getWord(): Resource<WordResult>

    suspend fun getWordList(query: String): Resource<KeyResult>

    suspend fun signIn(email : String, password : String) : Resource<AuthResult>

    suspend fun signUp(email : String, password : String,userName : String) : Resource<AuthResult>

    suspend fun updateIsActive(): Boolean

    suspend fun addActiveUsers(isActive: Boolean) : Resource<Int>

    suspend fun makeMatch(number: Int, word: String): Resource<Pair<Boolean, String>>

    suspend fun navigateCompet(number: Int): Resource<Pair<String, String>>

    suspend fun addAnswerFStore(answer: String, isTruth: Boolean, isHome: Boolean)

    suspend fun getAnswerFStore(isHome: Boolean): Resource<String>

    suspend fun deleteAnswerFStore(isHome: Boolean)

    suspend fun addCompettoUser(word: String, isWon: String)

    suspend fun deleteCompet()

    suspend fun getUserCompetitions(): Resource<List<String>>
}