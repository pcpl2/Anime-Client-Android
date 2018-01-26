package com.github.pcpl2.animeClient.services

import com.github.pcpl2.animeClient.callbacks.animeUpdateDataCallback
import com.github.pcpl2.animeClient.domain.AnimeEntry
import com.github.pcpl2.animeClient.domain.EpisodeEntry

/**
 * Created by patry on 25.01.2018.
 */
interface AnimeServiceImpl {

    val serviceId: String
    val domain: String

    val animeList: ArrayList<AnimeEntry>
    var selectedAnime: AnimeEntry?

    val episodeList: ArrayList<EpisodeEntry>
    var selectedEpisode: EpisodeEntry?

    fun updateAnimeList(callback: animeUpdateDataCallback, force: Boolean?) {}

    fun setCurrentAnime(animeId: String): Boolean {
        if(selectedAnime != null && selectedAnime?.id == animeId) {
            return true
        }

        val anime = animeList.find { it.id == animeId }

        return if (anime != null) {
            selectedAnime = anime
            updateCurrentAnimeData()
            true
        } else {
            false
        }
    }

    fun updateCurrentAnimeData() {}

    fun setCurrentEpisode(episodeId: String): Boolean {
        if(selectedEpisode != null && selectedEpisode?.id == episodeId) {
            return true
        }

        val episode = episodeList.find { it.id == episodeId }

        return if (episode != null) {
            selectedEpisode = episode
            updateCurrentEpisodeData()
            true
        } else {
            false
        }
    }

    fun updateCurrentEpisodeData() {}
}