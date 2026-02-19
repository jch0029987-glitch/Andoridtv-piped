package com.example.pipetv.network

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Main Video Object for Search Results
data class InvidiousVideo(
    val title: String,
    val videoId: String,
    val thumbnailUrl: String = "" // We will populate this via rewrite
)

// Detailed Video Data for Playback & Quality Selection
data class InvidiousVideoData(
    val formatStreams: List<InvidiousStream>
)

data class InvidiousStream(
    val url: String,
    val qualityLabel: String, // e.g., "720p"
    val resolution: String,   // e.g., "1280x720"
    val container: String     // e.g., "mp4"
)

class InvidiousRepository {
    private val client = OkHttpClient()
    private val gson = Gson()
    private val baseUrl = "http://10.78.240.3:3000"

    /**
     * Search for videos and force thumbnails to be proxied locally for PdaNet stealth.
     */
    suspend fun searchVideos(query: String): List<InvidiousVideo> = withContext(Dispatchers.IO) {
        val url = "$baseUrl/api/v1/search?q=${query.replace(" ", "+")}"
        val request = Request.Builder().url(url).build()

        try {
            val response = client.newCall(request).execute()
            val json = response.body?.string() ?: ""
            val type = object : TypeToken<List<InvidiousVideo>>() {}.type
            val videos: List<InvidiousVideo> = gson.fromJson(json, type)

            // Force thumbnails to go through the local phone/server proxy
            videos.map { video ->
                video.copy(
                    thumbnailUrl = "$baseUrl/vi/${video.videoId}/maxresdefault.jpg"
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Fetches detailed video metadata, including all available quality streams.
     * Passing local=true ensures the streams are proxied through the server.
     */
    suspend fun getVideoData(videoId: String): InvidiousVideoData? = withContext(Dispatchers.IO) {
        val url = "$baseUrl/api/v1/videos/$videoId?local=true"
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
     * Helper to get the default/best starting URL.
     */
    suspend fun getStreamUrl(videoId: String): String? {
        val data = getVideoData(videoId)
        // Prefer 720p as a safe starting point for PdaNet hotspots
        return data?.formatStreams?.firstOrNull { it.qualityLabel == "720p" }?.url 
            ?: data?.formatStreams?.firstOrNull()?.url
    }
}
