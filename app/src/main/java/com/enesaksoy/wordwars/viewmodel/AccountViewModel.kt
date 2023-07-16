package com.enesaksoy.wordwars.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enesaksoy.wordwars.repo.WordRepository
import com.enesaksoy.wordwars.util.Resource
import kotlinx.coroutines.launch

class AccountViewModel (private val repo: WordRepository): ViewModel() {

    private val getuserCompet = MutableLiveData<Resource<List<String>>>()
    val getUserCompet : LiveData<Resource<List<String>>>
    get() = getuserCompet

    fun getUserCompet(){
        viewModelScope.launch {
            getuserCompet.value = repo.getUserCompetitions()
        }
    }
}