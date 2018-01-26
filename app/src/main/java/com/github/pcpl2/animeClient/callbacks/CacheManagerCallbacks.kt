package com.github.pcpl2.animeClient.callbacks

import com.github.pcpl2.animeClient.domain.AnimeEntry

/**
 * Created by patry on 26.01.2018.
 */

interface CachceManagerGetData {
    fun onComplete(data: ArrayList<AnimeEntry>?)
}