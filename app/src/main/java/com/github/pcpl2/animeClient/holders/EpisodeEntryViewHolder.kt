package com.github.pcpl2.animeClient.holders

import android.support.v7.widget.RecyclerView
import android.view.View
import com.github.pcpl2.animeClient.callbacks.EpisodeListFragmentElementClicked
import com.github.pcpl2.animeClient.domain.EpisodeEntry
import kotlinx.android.synthetic.main.episode_entry_row.view.*

/**
 * Created by patry on 27.01.2018.
 */
class EpisodeEntryViewHolder(itemView: View, private val selectedItemCallback: EpisodeListFragmentElementClicked) : RecyclerView.ViewHolder(itemView) {

    fun bind(episodeEntry: EpisodeEntry) {
        itemView.episode_title.text = episodeEntry.title
        itemView.episode_title.isSelected = true

        itemView.setOnClickListener({ _ ->
            selectedItemCallback.onClicked(episodeEntry)
        })
    }
}
