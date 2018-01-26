package com.github.pcpl2.animeClient.services

import com.github.pcpl2.animeClient.callbacks.AnimeUpdateDataCallback
import com.github.pcpl2.animeClient.domain.AnimeEntry
import com.github.pcpl2.animeClient.domain.EpisodeEntry

/**
 * Created by patry on 25.01.2018.
 */
interface AnimeServiceImpl {

    val serviceId: String
    val domain: String

    var workThread: Thread?

    fun updateAnimeList(callback: AnimeUpdateDataCallback, force: Boolean?) {}

    fun updateCurrentAnimeData() {}

    fun updateCurrentEpisodeData() {}
}