package com.example.pipetv.network

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InvidiousRepository {
    private val client = OkHttpClient()
    private val gson = Gson()
    private val baseUrl = "http://10.78.240.3:3000"

    suspend fun searchVideos(query: String): List<InvidiousVideo> = withContext(Dispatchers.IO) {
        val url = "$baseUrl/api/v1/search?q=${query.replace(" ", "+")}"
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/121.0.0.0")
            .build()

        try {
            val response = client.newCall(request).execute()
            val json = response.body?.string() ?: ""
            val type = object : TypeToken<List<InvidiousVideo>>() {}.type
            val videos: List<InvidiousVideo> = gson.fromJson(json, type) ?: emptyList()

            // Rewrite thumbnails to local proxy for PdaNet stealth
            videos.map { it.copy(thumbnailUrl = "$baseUrl/vi/${it.videoId}/maxresdefault.jpg") }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getVideoData(videoId: String): InvidiousVideoData? = withContext(Dispatchers.IO) {
        // We force quality=dash and local=true to ensure the server tunnels the bits
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

    suspend fun getStreamUrl(videoId: String, preferredHeight: Int = 720): String? {
        val data = getVideoData(videoId) ?: return null
        
        // Strategy: 1. Try preferred height MP4, 2. Try any MP4, 3. Take whatever is first
        return data.formatStreams.firstOrNull { 
            it.qualityLabel.contains("${preferredHeight}p") && it.container == "mp4" 
        }?.url 
        ?: data.formatStreams.firstOrNull { it.container == "mp4" }?.url
        ?: data.formatStreams.firstOrNull()?.url
    }
}
