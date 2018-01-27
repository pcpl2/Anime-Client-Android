package com.github.pcpl2.animeClient.services

import com.github.pcpl2.animeClient.callbacks.UpdateAnimeListCallback
import com.github.pcpl2.animeClient.callbacks.UpdateEpisodeListCallback
import com.github.pcpl2.animeClient.callbacks.UpdatePlayersListCallback

/**
 * Created by patry on 25.01.2018.
 */
interface AnimeServiceImpl {

    val serviceId: String
    val domain: String

    var workThread: Thread?

    fun updateAnimeList(callbackUpdate: UpdateAnimeListCallback, force: Boolean?) {}

    fun updateEpisodeList(episodeUrl: String?, callback: UpdateEpisodeListCallback) {}

    fun updatePlayersList(episodeUrl: String?, callback: UpdatePlayersListCallback) {}
}