package com.github.pcpl2.animeClient.managers

/**
 * Created by patry on 26.01.2018.
 */

object CacheManager {
    private var instance: CacheManagerImpl? = null

    fun getInstance(): CacheManagerImpl {
        if (instance == null) {
            instance = CacheManagerImpl()
        }
        return instance!!
    }
}
