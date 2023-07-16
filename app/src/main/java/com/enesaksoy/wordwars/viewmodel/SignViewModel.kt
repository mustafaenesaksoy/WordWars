package com.enesaksoy.wordwars.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enesaksoy.wordwars.repo.WordRepository
import com.enesaksoy.wordwars.util.Resource
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.launch

class SignViewModel(
    private val repo: WordRepository
    ): ViewModel() {

    private val authresult = MutableLiveData<Resource<AuthResult>>()
    val authResult: LiveData<Resource<AuthResult>>
    get() = authresult

    fun signIn(email: String, password: String){
        viewModelScope.launch {
            authresult.value = repo.signIn(email, password)
        }
    }

    fun signUp(email: String, password: String,userName: String){
        viewModelScope.launch {
            authresult.value = repo.signUp(email, password, userName)
        }
    }
}