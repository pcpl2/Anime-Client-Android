package com.github.pcpl2.animeClient.domain

/**
 * Created by patry on 24.01.2018.
 */

data class AnimeEntry(val id: String, val url: String, val title: String, val img: String, val desc: String)

data class EpisodeEntry(val id: String, val url: String, val title: String, var players: Array<PlayerEntry>)

data class PlayerEntry(val id: String, val url: String, val lang: String, val name: String, val desc: String, val referer: String)