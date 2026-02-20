package com.example.pipetv.network

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder

data class InvidiousVideo(
    val title: String? = null,
    val videoId: String? = null,
    val author: String? = null,
    val viewCountText: String? = null,
    val videoThumbnails: List<Thumbnail>? = null
)

data class Thumbnail(val url: String)

data class VideoDetails(
    val formatStreams: List<StreamFormat>? = null,
    val adaptiveFormats: List<StreamFormat>? = null
)

data class StreamFormat(val url: String, val container: String?, val encoding: String?)

class InvidiousRepository {
    private val client = OkHttpClient()
    private val gson = Gson()
    private val baseUrl = "http://10.78.240.3:3000"

    suspend fun getTrendingVideos(): List<InvidiousVideo> = fetchVideos("$baseUrl/api/v1/trending")
    
    suspend fun searchVideos(query: String): List<InvidiousVideo> {
        val encoded = URLEncoder.encode(query, "UTF-8")
        return fetchVideos("$baseUrl/api/v1/search?q=$encoded")
    }

    private suspend fun fetchVideos(url: String): List<InvidiousVideo> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: return@withContext emptyList()
                val type = object : com.google.gson.reflect.TypeToken<List<InvidiousVideo>>() {}.type
                val list = gson.fromJson<List<InvidiousVideo>>(body, type)
                // Filter out broken items that cause crashes
                list?.filter { !it.videoId.isNullOrEmpty() } ?: emptyList()
            }
        } catch (e: Exception) { emptyList() }
    }

    suspend fun getVideoStreamUrl(videoId: String): String? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url("$baseUrl/api/v1/videos/$videoId?local=true").build()
            client.newCall(request).execute().use { response ->
                val details = gson.fromJson(response.body?.string(), VideoDetails::class.java)
                // Prefer MP4 for stability on hotspot; fall back to anything available
                details.formatStreams?.firstOrNull { it.container == "mp4" }?.url 
                    ?: details.adaptiveFormats?.firstOrNull()?.url
            }
        } catch (e: Exception) { null }
    }
}
