package com.example.pipetv.network

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder

class InvidiousRepository {
    private val client = OkHttpClient()
    private val gson = Gson()
    private val baseUrl = "http://10.78.240.3:3000"

    /**
     * Search for videos and force thumbnails to be proxied locally for PdaNet stealth.
     */
    suspend fun searchVideos(query: String): List<InvidiousVideo> = withContext(Dispatchers.IO) {
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val url = "$baseUrl/api/v1/search?q=$encodedQuery"
        val request = Request.Builder().url(url).build()

        try {
            val response = client.newCall(request).execute()
            val json = response.body?.string() ?: ""
            val type = object : TypeToken<List<InvidiousVideo>>() {}.type
            val videos: List<InvidiousVideo> = gson.fromJson(json, type) ?: emptyList()

            // Map and fix thumbnail paths for stealth
            videos.map { it.copy(thumbnailUrl = "$baseUrl/vi/${it.videoId}/maxresdefault.jpg") }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Fetches detailed video metadata.
     */
    suspend fun getVideoData(videoId: String): InvidiousVideoData? = withContext(Dispatchers.IO) {
        val url = "$baseUrl/api/v1/videos/$videoId?local=true&quality=dash"
        val request = Request.Builder().url(url).build()

        try {
            val response = client.newCall(request).execute()
            val json = response.body?.string() ?: ""
            gson.fromJson(json, InvidiousVideoData::class.java)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Fetches the best possible stream URL and fixes Malformed URL errors.
     */
    suspend fun getStreamUrl(videoId: String, preferredHeight: Int = 720): String? {
        val data = getVideoData(videoId) ?: return null
        
        val rawUrl = data.formatStreams.firstOrNull { 
            it.qualityLabel.contains("${preferredHeight}p") && it.container == "mp4" 
        }?.url 
        ?: data.formatStreams.firstOrNull { it.container == "mp4" }?.url
        ?: data.formatStreams.firstOrNull()?.url

        // Fix relative paths provided by the Invidious server
        return when {
            rawUrl == null -> null
            rawUrl.startsWith("http") -> rawUrl
            rawUrl.startsWith("/") -> "$baseUrl$rawUrl"
            else -> "$baseUrl/$rawUrl"
        }
    }
}
