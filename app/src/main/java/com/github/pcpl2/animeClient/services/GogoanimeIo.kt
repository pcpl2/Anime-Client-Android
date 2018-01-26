package com.github.pcpl2.animeClient.services

/**
 * Created by patry on 25.01.2018.
 */

object GogoanimeIo {
    private var instance: GogoanimeIoImpl? = null

    fun getInstance(): GogoanimeIoImpl {
        if (instance == null) {
            instance = GogoanimeIoImpl()
        }
        return instance!!
    }
}