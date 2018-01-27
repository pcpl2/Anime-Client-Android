package com.github.pcpl2.animeClient.services

import android.os.Process
import android.util.Log
import com.github.pcpl2.animeClient.callbacks.CachceManagerGetData
import com.github.pcpl2.animeClient.callbacks.UpdateAnimeListCallback
import com.github.pcpl2.animeClient.callbacks.UpdateEpisodeListCallback
import com.github.pcpl2.animeClient.callbacks.UpdatePlayersListCallback
import com.github.pcpl2.animeClient.domain.AnimeEntry
import com.github.pcpl2.animeClient.domain.EpisodeEntry
import com.github.pcpl2.animeClient.domain.PlayerEntry
import com.github.pcpl2.animeClient.managers.CacheManager
import com.github.pcpl2.animeClient.managers.CacheManagerImpl
import okhttp3.ResponseBody
import java.io.IOException
import org.jsoup.Jsoup
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.Retrofit
import retrofit2.http.QueryMap


/**
 * Created by patry on 24.01.2018.
 */

class GogoanimeIoImpl : AnimeServiceImpl {

    private interface GogoanimeIoService {
        @Headers(
                "Accept: text/html",
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3165.0 Safari/537.36",
                "Cache-Control: no-cache"
        )
        @GET("anime-list.html")
        fun getAnimeList(@QueryMap options: Map<String, String>): Call<ResponseBody>

        @Headers(
                "Accept: text/html",
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3165.0 Safari/537.36",
                "Cache-Control: no-cache"
        )
        @GET("{url}")
        fun getAnimePage(@Path("url") url: String): Call<ResponseBody>

        @Headers(
                "Accept: text/html",
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3165.0 Safari/537.36",
                "Cache-Control: no-cache"
        )
        @GET("load-list-episode")
        fun getEpisodeList(@QueryMap options: Map<String, String>): Call<ResponseBody>

        @Headers(
                "Accept: text/html",
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/62.0.3165.0 Safari/537.36",
                "Cache-Control: no-cache"
        )
        @GET("{url}")
        fun getEpisodePage(@Path("url") url: String): Call<ResponseBody>
    }

    override val serviceId = "gogoanimeIo"
    override val domain = "https://ww5.gogoanime.io/"

    private val retrofit = Retrofit.Builder().baseUrl(domain).build()
    private val service = retrofit.create(GogoanimeIoService::class.java)

    private val cacheManager: CacheManagerImpl = CacheManager.getInstance()

    override var workThread: Thread? = null


    override fun updateAnimeList(callbackUpdate: UpdateAnimeListCallback, force: Boolean?) {
        if (workThread != null && workThread?.isAlive!!) {
            workThread?.interrupt()
        }

        workThread = Thread(Runnable {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
            cacheManager.getFromCache(serviceId = serviceId, callback = object : CachceManagerGetData {
                override fun onComplete(data: ArrayList<AnimeEntry>?) {
                    if (data != null) {
                        updateAnimeComplete(data, callbackUpdate, fromCache = true)
                    } else {
                        updateAnimeRunRequest(page = 1, animeList = arrayListOf(), callbackUpdate = callbackUpdate)
                    }
                }
            })
        })
        workThread?.start()
    }

    private fun updateAnimeRunRequest(page: Int, animeList: ArrayList<AnimeEntry>, callbackUpdate: UpdateAnimeListCallback) {
        val options: Map<String, String> = mapOf(Pair("page", page.toString()))

        service.getAnimeList(options).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                if (response != null && response.isSuccessful) {
                    val body = response.body()?.string()
                    try {
                        val doc = Jsoup.parse(body)

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
                            updateAnimeComplete(animeList = animeList, callbackUpdate = callbackUpdate)
                        } else {
                            updateAnimeRunRequest(page = page + 1, animeList = animeList, callbackUpdate = callbackUpdate)
                        }
                    } catch (e: IOException) {
                        callbackUpdate.onError(e.message!!)
                    }
                } else {
                    if (response != null) {
                        callbackUpdate.onError(response.errorBody().toString())
                    } else {
                        callbackUpdate.onError("unknown error")
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                if (t != null) callbackUpdate.onError(t.message!!)
            }
        })
    }

    private fun updateAnimeComplete(animeList: ArrayList<AnimeEntry>, callbackUpdate: UpdateAnimeListCallback, fromCache: Boolean = false) {
        Log.d("", "complete")
        Log.d("", animeList.size.toString())
        if (!fromCache) {
            cacheManager.addCache(serviceId, animeList)
        }
        callbackUpdate.onComplete(animeList)
    }

    override fun updateEpisodeList(episodeUrl: String?, callback: UpdateEpisodeListCallback) {
        if (workThread != null && workThread?.isAlive!!) {
            workThread?.interrupt()
        }

        workThread = Thread(Runnable {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
            getAnimePage(animeUrl = episodeUrl, callback = callback)
        })
        workThread?.start()
    }

    private fun getAnimePage(animeUrl: String?, callback: UpdateEpisodeListCallback) {
        service.getAnimePage(animeUrl!!).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                if (response != null && response.isSuccessful) {
                    val body = response.body()?.string()

                    try {
                        val doc = Jsoup.parse(body)
                        val moveId = doc.select("#movie_id")[0].`val`()
                        val lastEpNumber = doc.select(".anime_video_body #episode_page li:last-child a").attr("ep_end")
                        if ((moveId != null && moveId.isNotEmpty()) && (lastEpNumber != null && lastEpNumber.isNotEmpty())) {
                            getEpisodeList(moveId = moveId, lastEpNumber = lastEpNumber, callback = callback)
                        } else {
                            callback.onError("unknown error")
                        }
                    } catch (e: IOException) {
                        callback.onError(e.message!!)
                    }

                } else {
                    if (response != null) {
                        callback.onError(response.errorBody().toString())
                    } else {
                        callback.onError("unknown error")
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                if (t != null) callback.onError(t.message!!)
            }
        })
    }

    private fun getEpisodeList(moveId: String, lastEpNumber: String, callback: UpdateEpisodeListCallback) {
        val options: Map<String, String> = mapOf(Pair("ep_start", "0"), Pair("ep_end", lastEpNumber), Pair("id", moveId), Pair("default_ep", "0"))
        service.getEpisodeList(options).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                if (response != null && response.isSuccessful) {
                    val body = response.body()?.string()
                    try {
                        val episodeList = arrayListOf<EpisodeEntry>()
                        val elements = Jsoup.parse(body).select("#episode_related")[0].children()
                        for (item in elements) {
                            val urlWithSlash = item.child(0).attr("href")
                            val url = urlWithSlash.split("/").last()
                            val id = urlWithSlash.split("/").last()
                            val title = item.child(0).child(0).text().trim()
                            episodeList.add(EpisodeEntry(id = id, url = url, title = title))
                        }

                        callback.onComplete(episodeList = episodeList)
                    } catch (e: IOException) {
                        callback.onError(e.message!!)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                if (t != null) callback.onError(t.message!!)
            }
        })
    }

    override fun updatePlayersList(episodeUrl: String?, callback: UpdatePlayersListCallback) {
        if (workThread != null && workThread?.isAlive!!) {
            workThread?.interrupt()
        }

        workThread = Thread(Runnable {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
            getEpisodePage(episodeUrl = episodeUrl, callback = callback)
        })
        workThread?.start()
    }

    private fun getEpisodePage(episodeUrl: String?, callback: UpdatePlayersListCallback) {
        service.getAnimePage(episodeUrl!!).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?) {
                if (response != null && response.isSuccessful) {
                    val body = response.body()?.string()
                    try {
                        val playersList = arrayListOf<PlayerEntry>()
                        val elements = Jsoup.parse(body).select(".anime_muti_link ul")[0].children()

                        var i = 0
                        for (item in elements) {
                            if (item.attr("class") != "anime") {
                                val video = item.child(0)
                                var videoUrl = video.attr("data-video")
                                if (!videoUrl.matches(Regex("""^(http|https)://.*${'$'}"""))) {
                                    videoUrl = "https:" + videoUrl
                                }
                                val videoName = video.textNodes()[0].text()
                                val videoId = removeSpecialCharacters(videoName.toLowerCase())

                                playersList.add(PlayerEntry(id = videoId, url = videoUrl.toString(), lang = "EN", name = videoName, desc = "", referer = ""))
                                i++
                            }
                        }
                        callback.onComplete(playersList)
                    } catch (e: IOException) {
                        callback.onError(e.message!!)
                    }
                } else {
                    if (response != null) {
                        callback.onError(response.errorBody().toString())
                    } else {
                        callback.onError("unknown error")
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?) {
                if (t != null) callback.onError(t.message!!)
            }
        })
    }

    private fun removeSpecialCharacters(str: String): String {
        val regex = Regex("[^A-Za-z0-9]")
        return regex.replace(str, "")
    }
}
