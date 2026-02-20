package com.example.pipetv.network

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder

// --- Data Models for Invidious ---
data class InvidiousVideo(
    val title: String,
    val videoId: String,
    val author: String,
    val viewCountText: String,
    val videoThumbnails: List<Thumbnail>
)

data class Thumbnail(
    val url: String,
    val width: Int,
    val height: Int
)

data class VideoDetails(
    val formatStreams: List<StreamFormat>?,
    val adaptiveFormats: List<StreamFormat>?
)

data class StreamFormat(
    val url: String,
    val qualityLabel: String?,
    val container: String?,
    val encoding: String?
)

// --- Repository Logic ---
class InvidiousRepository {
    private val client = OkHttpClient()
    private val gson = Gson()
    private val baseUrl = "http://10.78.240.3:3000" // Your local instance

    /**
     * Fetches the trending/popular feed for the Home screen
     */
    suspend fun getTrendingVideos(): List<InvidiousVideo> = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("$baseUrl/api/v1/trending")
                .build()

            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: return@withContext emptyList()
                val type = object : com.google.gson.reflect.TypeToken<List<InvidiousVideo>>() {}.type
                gson.fromJson(body, type)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Searches for videos
     */
    suspend fun searchVideos(query: String): List<InvidiousVideo> = withContext(Dispatchers.IO) {
        try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val request = Request.Builder()
                .url("$baseUrl/api/v1/search?q=$encodedQuery")
                .build()

            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: return@withContext emptyList()
                val type = object : com.google.gson.reflect.TypeToken<List<InvidiousVideo>>() {}.type
                gson.fromJson(body, type)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Gets the actual playable stream URL.
     * Uses local=true to proxy the video data through your Invidious instance.
     */
    suspend fun getVideoStreamUrl(videoId: String): String? = withContext(Dispatchers.IO) {
        try {
            // We use local=true to avoid carrier hotspot blocks on direct YT domains
            val request = Request.Builder()
                .url("$baseUrl/api/v1/videos/$videoId?local=true")
                .build()

            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: return@withContext null
                val details = gson.fromJson(body, VideoDetails::class.java)

                // 1. Try to find a standard mp4 stream first (easiest for ExoPlayer)
                val mp4Stream = details.formatStreams?.firstOrNull { it.container == "mp4" }
                if (mp4Stream != null) return@withContext mp4Stream.url

                // 2. Fallback to the first available adaptive stream if mp4 isn't found
                return@withContext details.adaptiveFormats?.firstOrNull()?.url
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
