package com.github.pcpl2.animeClient.callbacks

import com.github.pcpl2.animeClient.domain.AnimeEntry
import com.github.pcpl2.animeClient.domain.EpisodeEntry

/**
 * Created by patry on 26.01.2018.
 */

interface AnimeListFragmentElementClicked {
    fun onClicked(animeEntry: AnimeEntry)
}

interface EpisodeListFragmentElementClicked {
    fun onClicked(episodeEntry: EpisodeEntry)
}
