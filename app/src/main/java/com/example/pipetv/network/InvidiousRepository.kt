package com.example.pipetv.network

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URLEncoder

// Data models to match Invidious API response
data class InvidiousVideo(
    val title: String,
    val videoId: String,
    val author: String,
    val thumbnailUrl: String,
    val viewCountText: String? = ""
)

data class InvidiousVideoData(
    val formatStreams: List<InvidiousStream>
)

data class InvidiousStream(
    val url: String,
    val container: String,
    val qualityLabel: String
)

class InvidiousRepository {
    private val client = OkHttpClient()
    private val gson = Gson()
    private val TAG = "PipeTV_Repo"
    
    // Your Self-Hosted Instance
    private val baseUrl = "http://10.78.240.3:3000"

    // Your GitHub for Update Checking
    private val GITHUB_RELEASE_URL = "https://api.github.com/repos/jch0029987-glitch/Andoridtv-piped/releases/latest"

    /**
     * Helper to build requests with necessary headers for self-hosted instances
     */
    private fun buildRequest(url: String) = Request.Builder()
        .url(url)
        .header("Accept", "application/json")
        .header("User-Agent", "PipeTV/1.0 (Android TV; jch0029987-glitch)")
        .build()

    /**
     * Checks GitHub for the latest APK release
     */
    suspend fun checkForUpdates(): Pair<String, String>? = withContext(Dispatchers.IO) {
        try {
            val request = buildRequest(GITHUB_RELEASE_URL)
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext null
            
            val bodyString = response.body?.string() ?: return@withContext null
            val releaseMap: Map<String, Any> = gson.fromJson(bodyString, object : TypeToken<Map<String, Any>>() {}.type)
            
            val tagName = releaseMap["tag_name"] as? String ?: return@withContext null
            val assets = releaseMap["assets"] as? List<Map<String, Any>>
            
            // Look for the first file ending in .apk
            val apkUrl = assets?.firstOrNull { 
                (it["name"] as? String)?.endsWith(".apk") == true 
            }?.get("browser_download_url") as? String
            
            if (apkUrl != null) Pair(tagName, apkUrl) else null
        } catch (e: Exception) {
            Log.e(TAG, "Update check failed", e)
            null
        }
    }

    /**
     * Fetches trending videos from your self-hosted instance
     */
    suspend fun getTrendingVideos(): List<InvidiousVideo> = withContext(Dispatchers.IO) {
        try {
            val url = "$baseUrl/api/v1/trending?region=US"
            val response = client.newCall(buildRequest(url)).execute()
            val body = response.body?.string() ?: ""
            
            val videos: List<InvidiousVideo> = gson.fromJson(body, object : TypeToken<List<InvidiousVideo>>() {}.type)
            
            // Map thumbnails to MQ (Medium Quality) to save carrier hotspot data
            videos.map { it.copy(thumbnailUrl = "$baseUrl/vi/${it.videoId}/mqdefault.jpg") }
        } catch (e: Exception) {
            Log.e(TAG, "Trending fetch failed", e)
            emptyList()
        }
    }

    /**
     * Searches videos on your self-hosted instance
     */
    suspend fun searchVideos(query: String): List<InvidiousVideo> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val url = "$baseUrl/api/v1/search?q=$encodedQuery"
            val response = client.newCall(buildRequest(url)).execute()
            val body = response.body?.string() ?: ""
            
            val videos: List<InvidiousVideo> = gson.fromJson(body, object : TypeToken<List<InvidiousVideo>>() {}.type)
            videos.map { it.copy(thumbnailUrl = "$baseUrl/vi/${it.videoId}/mqdefault.jpg") }
        } catch (e: Exception) {
            Log.e(TAG, "Search failed", e)
            emptyList()
        }
    }

    /**
     * Gets the direct stream URL for playback
     */
    suspend fun getStreamUrl(videoId: String): String? = withContext(Dispatchers.IO) {
        try {
            // local=true tells Invidious to proxy the stream through your self-hosted server
            val url = "$baseUrl/api/v1/videos/$videoId?local=true"
            val response = client.newCall(buildRequest(url)).execute()
            val body = response.body?.string() ?: return@withContext null
            
            val data = gson.fromJson(body, InvidiousVideoData::class.java)
            
            // Prefer MP4 for best Android TV compatibility
            val stream = data.formatStreams.firstOrNull { it.container == "mp4" }?.url 
                ?: data.formatStreams.firstOrNull()?.url
            
            // Ensure the URL is absolute
            if (stream != null && !stream.startsWith("http")) "$baseUrl$stream" else stream
        } catch (e: Exception) {
            Log.e(TAG, "Stream URL fetch failed", e)
            null
        }
    }
}
