package com.github.pcpl2.animeClient.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.pcpl2.animeClient.R
import com.github.pcpl2.animeClient.callbacks.EpisodeListFragmentElementClicked
import com.github.pcpl2.animeClient.fragments.EpisodeListFragment
import com.github.pcpl2.animeClient.domain.EpisodeEntry
import com.github.pcpl2.animeClient.holders.EpisodeEntryViewHolder

/**
 * Created by patry on 27.01.2018.
 */
class EpisodeListAdapter(private val episodeListFragment: EpisodeListFragment, private val selectedItemCallback: EpisodeListFragmentElementClicked) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class EpisodeListTypes {
        EPISODE_ENITIY
    }

    private val episodeList: ArrayList<EpisodeEntry> = arrayListOf()
    private val episodeListFiltered: ArrayList<EpisodeEntry> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        var viewHolder: RecyclerView.ViewHolder? = null

        when (viewType) {
            EpisodeListTypes.EPISODE_ENITIY.ordinal ->
                viewHolder = EpisodeEntryViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.episode_entry_row, parent, false), selectedItemCallback)

        }
        return viewHolder!!

    }

    override fun getItemCount(): Int {
        return episodeList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        when (getItemViewType(position)) {
            EpisodeListTypes.EPISODE_ENITIY.ordinal -> {
                val episodeViewHolder = holder as EpisodeEntryViewHolder
                episodeViewHolder.bind(episodeList[position])
            }
        }
    }

    fun _notifyDataSetChanged() {
        if (isEmpty() && (episodeListFragment.currentState != EpisodeListFragment.EpisodeListFragmentStatus.EMPTY)) {
            episodeListFragment.setState(EpisodeListFragment.EpisodeListFragmentStatus.EMPTY)
        } else {
            if (episodeListFragment.currentState == EpisodeListFragment.EpisodeListFragmentStatus.EMPTY) {
                episodeListFragment.setState(EpisodeListFragment.EpisodeListFragmentStatus.LOADED)
            }
        }
        notifyDataSetChanged()
    }

    fun addAll(episodeEntryList: List<EpisodeEntry>) {
        clear()
        episodeList.addAll(episodeEntryList)
        episodeList.reverse()
        //episodeList.sortBy { it.title }
        _notifyDataSetChanged()
    }

    fun clear() {
        episodeList.clear()
        episodeListFiltered.clear()
        _notifyDataSetChanged()
    }

    fun isEmpty(): Boolean {
        return itemCount == 0
    }
}
