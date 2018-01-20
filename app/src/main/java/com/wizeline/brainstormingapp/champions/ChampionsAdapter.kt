package com.wizeline.brainstormingapp.champions

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.wizeline.brainstormingapp.R
import com.wizeline.brainstormingapp.repository.ChampionMessage
import kotlinx.android.synthetic.main.champion_item.view.*

class ChampionsAdapter : RecyclerView.Adapter<ChampionsViewHolder>() {

    val champions = arrayListOf<ChampionMessage>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ChampionsViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.champion_item, parent, false))

    override fun onBindViewHolder(holder: ChampionsViewHolder, position: Int) = holder.bind(champions[position], position)

    override fun getItemCount() = champions.size

}

class ChampionsViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bind(message: ChampionMessage, position: Int) {
        itemView.text.text = message.message.text
        itemView.votes_up.text = itemView.context.getString(R.string.votes_up, message.upVotes)
        itemView.votes_down.text = itemView.context.getString(R.string.votes_down, -message.downVotes)
        itemView.author.text = message.message.email
        itemView.content.setBackgroundColor(brewColor(position))
    }

    private fun brewColor(position: Int): Int {
        return listOf(
                0xff4caf50.toInt(),
                0xff8bc34a.toInt(),
                0xffcddc39.toInt(),
                0xffffeb3b.toInt(),
                0xffffc107.toInt()
        )[position]
    }

}