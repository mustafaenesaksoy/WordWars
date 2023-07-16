package com.enesaksoy.wordwars.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.enesaksoy.wordwars.model.Item
import com.enesaksoy.wordwars.model.KeyResult
import com.enesaksoy.wordwars.model.Response
import com.enesaksoy.wordwars.model.WordResult
import com.enesaksoy.wordwars.repo.WordRepository
import com.enesaksoy.wordwars.util.Resource
import kotlinx.coroutines.launch
import kotlin.random.Random

class GameViewModel(private val repo: WordRepository): ViewModel() {

    private val getword = MutableLiveData<Resource<WordResult>>()
    val getWord: LiveData<Resource<WordResult>>
        get() = getword

    private val getWordList = MutableLiveData<Resource<KeyResult>>()
    val getwordList: LiveData<Resource<KeyResult>>
        get() = getWordList

    private val ishome = MutableLiveData<Boolean>()
    val isHome : LiveData<Boolean>
        get() = ishome

    private val getAnswerFStore = MutableLiveData<Resource<String>>()
    val getAnswerFstore : LiveData<Resource<String>>
        get() = getAnswerFStore

    private val resultCheck = MutableLiveData<Boolean>()

    private val itemList = MutableLiveData<List<Item>>()

    private var countDownTimer : CountDownTimer? = null

    var timer = MutableLiveData<Long>()

    val isFinished = MutableLiveData<Boolean>()

    val currentUser = MutableLiveData<Int>()

    val isCheckWon = MutableLiveData<Boolean>()



    fun getWord(){
        viewModelScope.launch {
            getword.value = repo.getWord()
        }
    }


    fun getWordList(query: String){
        getWordList.value = Resource.loading(null)
        viewModelScope.launch {
            getWordList.value = repo.getWordList(query)
        }
    }


    fun responseConverttoList(response: Response){
        val items = response.items
        var i = 0
        val itemlist = ArrayList<Item>()
        while (i < items.size){
            itemlist.add(items[i])
            i++
        }
        itemList.value = itemlist
        randomOrder()
    }


    fun startTimer(time: Long){
        countDownTimer = object : CountDownTimer(time, 1000){
            override fun onTick(millisUntilFinished: Long) {
                timer.value = millisUntilFinished / 1000
            }

            override fun onFinish() {
                isFinished.value = currentUser.value == 1
                if (isHome.value != null){
                    isCheckWon.value = isHome.value!!
                }

            }
        }.start()
    }

    fun stopTimer(){
        countDownTimer?.cancel()
    }


    fun checkListforItem(checkString: String) : String{
        for (item in itemList.value!!){
            resultCheck.value = item.item.contains(checkString, ignoreCase = true)
            if (resultCheck.value == true){
                return item.item
            }
        }
        return ""
    }

    private fun randomOrder(){
        currentUser.value = Random.nextInt(1,3)
    }

    fun changeOrder(){
        if (currentUser.value == 1){
            currentUser.value = 2
        }else{
            currentUser.value = 1
        }
    }

    private var count : Int = 0
    fun offlineAnswer(level: String): String{

        if (itemList.value!!.isNotEmpty()){
            val rand = Random.nextInt(0,itemList.value!!.size)

            if (level.equals("Easy")){
                if (count < 4){
                    count++
                    return itemList.value!![rand].item
                }
            }else if(level.equals("Normal")){
                if (count < 6){
                    count++
                    return itemList.value!![rand].item
                }
            }else{
                if (count < 8){
                    count++
                    return itemList.value!![rand].item
                }
            }
            return ""
        }else{
            return ""
        }
    }

    fun isHome(isHome: Boolean){
        ishome.value = isHome
    }

    fun updateIsHome(){
        ishome.value = !isHome.value!!
    }

    fun addAnswerFStore(answer: String, isTruth: Boolean,isHome: Boolean){
        viewModelScope.launch {
            repo.addAnswerFStore(answer, isTruth, isHome)
        }
    }

    fun getAnswerFStore(isHome: Boolean){
        viewModelScope.launch{
            getAnswerFStore.value = repo.getAnswerFStore(isHome)
        }
    }

    fun deleteAnswerFStore(isHome: Boolean){
        viewModelScope.launch {
            repo.deleteAnswerFStore(isHome)
        }
    }

    fun addCompettoUser(word: String, isWon: String) {
        viewModelScope.launch {
            repo.addCompettoUser(word, isWon)
        }
    }

    fun deleteCompet(){
        viewModelScope.launch {
            repo.deleteCompet()
        }
    }

}