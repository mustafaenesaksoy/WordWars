package com.enesaksoy.wordwars.adapter

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.room.MapInfo
import com.enesaksoy.wordwars.databinding.GameRowBinding
import com.enesaksoy.wordwars.model.Answer

class GameAdapter :RecyclerView.Adapter<GameAdapter.GameHolder>() {

    private val answerList = ArrayList<Answer>()
    class GameHolder(val gameRowBinding: GameRowBinding): RecyclerView.ViewHolder(gameRowBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameHolder {
        val gameRowBinding = GameRowBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return GameHolder(gameRowBinding)
    }

    override fun getItemCount(): Int {
        return answerList.size
    }

    override fun onBindViewHolder(holder: GameHolder, position: Int) {
        val selectWord = answerList.get(position)
        if (selectWord.order == 1){
            holder.gameRowBinding.recyclerRowLinear.gravity = Gravity.START
            if (selectWord.result){
                holder.gameRowBinding.recyclerRowText.text = "${selectWord.word}(True)"
                holder.gameRowBinding.recyclerRowText.setBackgroundColor(Color.parseColor("#ffcb50"))
            }else{
                holder.gameRowBinding.recyclerRowText.text = "${selectWord.word}(False)"
                holder.gameRowBinding.recyclerRowText.setBackgroundColor(Color.parseColor("#ffcb50"))
            }
        }else{
            holder.gameRowBinding.recyclerRowLinear.gravity = Gravity.END
            if (selectWord.result){
                holder.gameRowBinding.recyclerRowText.text = "(True)${selectWord.word}"
                holder.gameRowBinding.recyclerRowText.setBackgroundColor(Color.parseColor("#cb712e"))
            }else{
                holder.gameRowBinding.recyclerRowText.text = "(False)${selectWord.word}"
                holder.gameRowBinding.recyclerRowText.setBackgroundColor(Color.parseColor("#cb712e"))
            }
        }
    }

    fun addWordtoList(answer: Answer, context: Context){
        for (answered in answerList){
            if(answered.word.contains(answer.word)){
                Toast.makeText(context, "are from the same word.", Toast.LENGTH_SHORT).show()
                return
            }
        }
        answerList.add(answer)
    }

    fun cleanWordList(){
        answerList.clear()
    }
}