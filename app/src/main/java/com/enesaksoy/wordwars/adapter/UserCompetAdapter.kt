package com.enesaksoy.wordwars.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.enesaksoy.wordwars.databinding.CompetitionsRowBinding

class UserCompetAdapter: RecyclerView.Adapter<UserCompetAdapter.UserCompetHolder>() {

    private val diffutil = object : DiffUtil.ItemCallback<String>(){
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.equals(newItem)
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.equals(newItem)
        }
    }

    val recyclerList = AsyncListDiffer(this, diffutil)

    var competList : List<String>
    get() = recyclerList.currentList
    set(value) = recyclerList.submitList(value)

    class UserCompetHolder(val binding : CompetitionsRowBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserCompetHolder {
        val binding = CompetitionsRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserCompetHolder(binding)
    }

    override fun getItemCount(): Int {
        return competList.size
    }

    override fun onBindViewHolder(holder: UserCompetHolder, position: Int) {
        if (position != 0) {
            val selectCompet = competList.get(position)
            val competItem = selectCompet.split(" ")
            holder.binding.wordText.text = competItem[0]
            holder.binding.resultText.text = competItem[1]
            if (competItem[1].equals("won")) {
                holder.binding.resultText.setTextColor(Color.parseColor("#528541"))
            } else if (competItem[1].equals("lose")) {
                holder.binding.resultText.setTextColor(Color.parseColor("#ff3724"))
            }
        }
    }
}