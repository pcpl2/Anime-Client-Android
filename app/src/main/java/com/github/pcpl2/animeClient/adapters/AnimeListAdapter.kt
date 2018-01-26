package com.github.pcpl2.animeClient.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.pcpl2.animeClient.R
import com.github.pcpl2.animeClient.domain.AnimeEntry
import com.github.pcpl2.animeClient.holders.AnimeEntityViewHolder


/**
 * Created by patry on 26.01.2018.
 */
class AnimeListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class AnimeListTypes {
        ANIME_ENITIY
    }

    private val animeList: ArrayList<AnimeEntry> = arrayListOf()
    private val animeListFiltered: ArrayList<AnimeEntry> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null

        when (viewType) {
            AnimeListTypes.ANIME_ENITIY.ordinal ->
                viewHolder = AnimeEntityViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.anime_enitiy_row, parent, false))

        }
        return viewHolder!!

    }

    override fun getItemCount(): Int {
        return animeList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when (getItemViewType(position)) {
            AnimeListTypes.ANIME_ENITIY.ordinal -> {
                val animeViewHolder = holder as AnimeEntityViewHolder
                animeViewHolder.bind(animeList[position])
            }
        }
    }

    fun addAll(animeEntryList: List<AnimeEntry>) {
        clear()
        animeList.addAll(animeEntryList)
        animeList.sortBy { it.title }
        notifyDataSetChanged()
    }

    fun clear() {
        animeList.clear()
        animeListFiltered.clear()
        notifyDataSetChanged()
    }

    fun isEmpty(): Boolean {
        return itemCount == 0
    }
}
