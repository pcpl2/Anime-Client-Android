package com.github.pcpl2.animeClient.callbacks

import com.github.pcpl2.animeClient.domain.AnimeEntry
import com.github.pcpl2.animeClient.domain.EpisodeEntry
import com.github.pcpl2.animeClient.domain.PlayerEntry

/**
 * Created by patry on 25.01.2018.
 */

interface UpdateAnimeListCallback {
    fun onComplete(animeList: ArrayList<AnimeEntry>)
    fun onError(message: String)
}

interface UpdateEpisodeListCallback {
    fun onComplete(episodeList: ArrayList<EpisodeEntry>)
    fun onError(message: String)
}

interface UpdatePlayersListCallback {
    fun onComplete(episodeList: ArrayList<PlayerEntry>)
    fun onError(message: String)
}
