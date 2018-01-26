package com.github.pcpl2.animeClient.managers

import android.content.Context
import android.os.Process
import com.github.pcpl2.animeClient.domain.AnimeEntry
import org.joda.time.DateTime
import org.joda.time.format.ISODateTimeFormat
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import com.google.gson.GsonBuilder
import android.util.Log
import com.github.pcpl2.animeClient.callbacks.CachceManagerGetData
import java.io.*
import com.google.gson.reflect.TypeToken
import org.joda.time.Hours


/**
 * Created by patry on 26.01.2018.
 */
class CacheManagerImpl {
    data class AnimeListCache(val ts: DateTime, val animeList: ArrayList<AnimeEntry>)

    internal class JodaDateTimeTypeAdapter : JsonSerializer<DateTime>, JsonDeserializer<DateTime> {
        @Throws(JsonParseException::class)
        override fun deserialize(json: JsonElement, typeOfT: Type,
                                 context: JsonDeserializationContext): DateTime {
            return DateTime.parse(json.asString)
        }

        override fun serialize(src: DateTime, typeOfSrc: Type,
                               context: JsonSerializationContext): JsonElement {
            return JsonPrimitive(ISODateTimeFormat
                    .dateTimeNoMillis()
                    .print(src))
        }
    }

    private var context: Context? = null

    private val filename = "AnimeClientServicesCache"

    private val gson = GsonBuilder().registerTypeAdapter(DateTime::class.java, JodaDateTimeTypeAdapter()).create()

    private val cacheAnimeMap: MutableMap<String, AnimeListCache> = mutableMapOf()

    private var backgroundSaveFileThread: Thread? = null
    private var backgroundReadFileThread: Thread? = null

    fun init(context: Context) {
        this.context = context.applicationContext
        readCacheFile()
    }

    fun addCache(serviceId: String, animeList: ArrayList<AnimeEntry>) {
        backgroundSaveFileThread?.join()
        val animeListCache = AnimeListCache(ts = DateTime.now(), animeList = animeList)
        cacheAnimeMap[serviceId] = animeListCache
        updateCacheFile()
    }

    fun getFromCache(serviceId: String, callback: CachceManagerGetData) {
        backgroundReadFileThread?.join()
        checkDateOfCache(serviceId = serviceId)
        callback.onComplete(cacheAnimeMap[serviceId]?.animeList)
    }

    private fun updateCacheFile() {
        backgroundSaveFileThread?.join()
        backgroundSaveFileThread = Thread(Runnable {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
            val json = gson.toJson(cacheAnimeMap)
            val file = File(context?.cacheDir, filename)
            val fw = FileWriter(file.absoluteFile)
            val bw = BufferedWriter(fw)
            bw.write(json)
            bw.close()
        })
        backgroundSaveFileThread?.start()
    }

    private fun readCacheFile() {
        backgroundReadFileThread?.join()
        backgroundReadFileThread = Thread(Runnable {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND)
            val file = File(context?.cacheDir, filename)
            val fr = FileReader(file.absoluteFile)
            val json = BufferedReader(fr).readLine()
            fr.close()
            val cacheAnimeMapType = object : TypeToken<Map<String, AnimeListCache>>() {}.type
            val obj = gson.fromJson<Map<String, AnimeListCache>>(json, cacheAnimeMapType)
            cacheAnimeMap.clear()
            cacheAnimeMap.putAll(obj)
        })
        backgroundReadFileThread?.start()
    }

    private fun checkDateOfCache(serviceId: String) {
        if(cacheAnimeMap.contains(serviceId)) {
            val hours = Hours.hoursBetween(cacheAnimeMap[serviceId]?.ts, DateTime.now().plusHours(36))
            if(hours.hours >= 24) {
                cacheAnimeMap.remove(serviceId)
                updateCacheFile()
            }
        }
    }
}
