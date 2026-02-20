package com.example.pipetv.network

import com.example.pipetv.PipeTVApp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InvidiousRepository(private val app: PipeTVApp) {
    private val gson = Gson()
    private val host = "http://10.78.240.3"
    private val apiBase = "$host/api/v1"

    suspend fun getTrendingVideos(): List<InvidiousVideo> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url("$apiBase/trending").build()
            app.okHttpClient.newCall(request).execute().use { response ->
                val json = response.body?.string() ?: return@withContext emptyList()
                val type = object : TypeToken<List<InvidiousVideo>>() {}.type
                gson.fromJson(json, type)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getVideoStreamUrl(videoId: String): String? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url("$apiBase/videos/$videoId").build()
            app.okHttpClient.newCall(request).execute().use { response ->
                val json = response.body?.string() ?: return@withContext null
                val detail = gson.fromJson(json, VideoDetail::class.java)
                
                // Get the first available stream
                val rawUrl = detail.adaptiveFormats?.firstOrNull()?.url 
                            ?: detail.formatStreams?.firstOrNull()?.url

                // Fix: Prepend the local IP if Invidious sends a relative path
                when {
                    rawUrl == null -> null
                    rawUrl.startsWith("http") -> rawUrl
                    else -> "$host$rawUrl"
                }
            }
        } catch (e: Exception) {
            null
        }
    }
}

data class InvidiousVideo(val title: String?, val videoId: String?, val author: String?, val videoThumbnails: List<Thumbnail>?)
data class Thumbnail(val url: String)
data class VideoDetail(val formatStreams: List<Stream>?, val adaptiveFormats: List<Stream>?)
data class Stream(val url: String)
