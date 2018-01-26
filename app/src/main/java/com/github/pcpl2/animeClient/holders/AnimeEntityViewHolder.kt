package com.github.pcpl2.animeClient.holders

import android.support.v7.widget.RecyclerView
import android.view.View
import com.github.pcpl2.animeClient.domain.AnimeEntry
import kotlinx.android.synthetic.main.anime_enitiy_row.view.*


/**
 * Created by patry on 26.01.2018.
 */
class AnimeEntityViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bind(animeEntry: AnimeEntry) {
        itemView.anime_title.text = animeEntry.title
        itemView.anime_desc.text = animeEntry.desc
        //TODO iamge loader

        itemView.setOnClickListener({ v ->
            //TODO next fragment and send params serviceId and animeId
        })
    }
}