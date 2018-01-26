package com.github.pcpl2.animeClient.services

import android.content.Context
import android.util.Log
import com.github.pcpl2.animeClient.callbacks.CachceManagerGetData
import com.github.pcpl2.animeClient.callbacks.animeUpdateDataCallback
import com.github.pcpl2.animeClient.domain.AnimeEntry
import com.github.pcpl2.animeClient.domain.EpisodeEntry
import com.github.pcpl2.animeClient.managers.CacheManager
import com.github.pcpl2.animeClient.managers.CacheManagerImpl
import java.io.IOException
import org.jsoup.Jsoup


/**
 * Created by patry on 24.01.2018.
 */

class GogoanimeIoImpl : AnimeServiceImpl {

    override val serviceId = "gogoanimeIo"
    override val domain = "https://ww4.gogoanime.io"

    override val animeList: ArrayList<AnimeEntry> = arrayListOf()
    override var selectedAnime: AnimeEntry? = null

    override val episodeList: ArrayList<EpisodeEntry> = arrayListOf()
    override var selectedEpisode: EpisodeEntry? = null

    private val cacheManager: CacheManagerImpl = CacheManager.getInstance()


    override fun updateAnimeList(callback: animeUpdateDataCallback, force: Boolean?) {
        animeList.clear()

        cacheManager.getFromCache(serviceId = serviceId, callback = object : CachceManagerGetData {
            override fun onComplete(data: ArrayList<AnimeEntry>?) {
                if (data != null) {
                    updateAnimeComplete(data, callback, fromCache = true)
                } else {
                    val startUrl = "$domain/anime-list.html?page="
                    updateAnimeRunRequest(startUrl = startUrl, page = 1, animeList = arrayListOf(), callback = callback)
                }
            }
        })
    }

    private fun updateAnimeRunRequest(startUrl: String, page: Int, animeList: ArrayList<AnimeEntry>, callback: animeUpdateDataCallback) {
        object : Thread() {
            override fun run() {
                try {
                    val doc = Jsoup.connect("$startUrl$page").get()

                    val base = doc.select("ul.listing")
                    val links = base.select("li")

                    for (link in links) {
                        val htmlObj2 = Jsoup.parse(link.attr("title"))
                        val htmlObj = link.child(0)

                        val url = htmlObj.attr("href")
                        val id = url.split("/").last()
                        val title = htmlObj2.select("a.bigChar")[0].text()
                        val img = htmlObj2.select("div.thumnail_tool img")[0].attr("src")
                        val desc = htmlObj2.select("p.sumer")[0].text().substring(13).trim()

                        animeList.add(AnimeEntry(id = id, url = url, title = title, img = img, desc = desc))
                    }

                    if (links.size < 136) {
                        updateAnimeComplete(animeList = animeList, callback = callback)
                    } else {
                        updateAnimeRunRequest(startUrl = startUrl, page = page + 1, animeList = animeList, callback = callback)
                    }
                } catch (e: IOException) {
                    callback.onError(e.message!!)
                }
            }
        }.start()
    }

    private fun updateAnimeComplete(animeList: ArrayList<AnimeEntry>, callback: animeUpdateDataCallback, fromCache: Boolean = false) {
        Log.d("", "complete")
        Log.d("", animeList.size.toString())
        if (!fromCache) {
            cacheManager.addCache(serviceId, animeList)
        }
        callback.onComplete(animeList)
    }

    override fun updateCurrentAnimeData() {
        super.updateCurrentAnimeData()
    }

    override fun updateCurrentEpisodeData() {
        super.updateCurrentEpisodeData()
    }
}