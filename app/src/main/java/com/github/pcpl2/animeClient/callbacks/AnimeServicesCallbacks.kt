package com.github.pcpl2.animeClient.callbacks

import com.github.pcpl2.animeClient.domain.AnimeEntry
import com.github.pcpl2.animeClient.domain.EpisodeEntry

/**
 * Created by patry on 25.01.2018.
 */

interface AnimeUpdateDataCallback {
    fun onComplete(animeList: ArrayList<AnimeEntry>)
    fun onError(message: String)
}

interface EpisodeUpdateDataCallback {
    fun onComplete(episodeList: ArrayList<EpisodeEntry>)
    fun onError(message: String)
}
