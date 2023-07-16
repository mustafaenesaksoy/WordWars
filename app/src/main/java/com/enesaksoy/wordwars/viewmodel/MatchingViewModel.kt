package com.enesaksoy.wordwars.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enesaksoy.wordwars.model.WordResult
import com.enesaksoy.wordwars.repo.WordRepository
import com.enesaksoy.wordwars.util.Resource
import com.enesaksoy.wordwars.util.Status
import kotlinx.coroutines.launch

class MatchingViewModel (private val repo: WordRepository): ViewModel() {

    private val isActive = MutableLiveData<Boolean>()
    val isactive : LiveData<Boolean>
    get() = isActive

    private val addActiveUser = MutableLiveData<Resource<Int>>()
    val addactiveUser : LiveData<Resource<Int>>
    get() = addActiveUser

    private val makematch = MutableLiveData<Resource<Pair<Boolean, String>>>()
    val makeMatch : LiveData<Resource<Pair<Boolean, String>>>
    get() = makematch

    private val navigatecompet = MutableLiveData<Resource<Pair<String, String>>>()
    val navigateCompet : LiveData<Resource<Pair<String, String>>>
    get() = navigatecompet

    private val getWord = MutableLiveData<Resource<WordResult>>()
    val getword: LiveData<Resource<WordResult>>
    get() = getWord

    var number : Int? = null

    var selectedWord : String? = null




    fun updateIsActive() {
        viewModelScope.launch {
            isActive.value = repo.updateIsActive()
        }
    }

    fun addActiveUser(isActive: Boolean) {
        viewModelScope.launch {
            addActiveUser.value = repo.addActiveUsers(isActive)
        }
    }

    fun makeMatch(word: String){
        viewModelScope.launch {
            makematch.value = repo.makeMatch(number!!, word)
        }
        selectedWord = word
    }


    fun navigateCompet(number: Int){
        viewModelScope.launch {
            navigatecompet.value = repo.navigateCompet(number)
        }
    }

    fun getWord(){
        viewModelScope.launch {
            getWord.value = repo.getWord()
        }
    }
}