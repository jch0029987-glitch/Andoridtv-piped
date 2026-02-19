package com.example.pipetv.network

import android.util.Log
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
    private val TAG = "PipeTV_Repo"

    // GitHub Repo Details - REPLACE with your actual username/repo
    private val GITHUB_API_URL = "https://api.github.com/repos/YOUR_USERNAME/Andoridtv-piped/releases/latest"

    /**
     * Checks GitHub for a new release.
     * Returns a Pair of (VersionTagName, DownloadUrl) if successful.
     */
    suspend fun checkForUpdates(): Pair<String, String>? = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(GITHUB_API_URL)
            .header("Accept", "application/vnd.github+json")
            .build()

        try {
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext null

            val json = response.body?.string() ?: ""
            val releaseMap: Map<String, Any> = gson.fromJson(json, object : TypeToken<Map<String, Any>>() {}.type)

            val tagName = releaseMap["tag_name"] as? String ?: return@withContext null
            
            // Find the APK in the assets list
            val assets = releaseMap["assets"] as? List<Map<String, Any>>
            val apkAsset = assets?.firstOrNull { 
                val name = it["name"] as? String ?: ""
                name.endsWith(".apk") 
            }
            
            val downloadUrl = apkAsset?.get("browser_download_url") as? String

            if (downloadUrl != null) {
                Log.d(TAG, "New build found: $tagName")
                Pair(tagName, downloadUrl)
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Update check failed: ${e.message}")
            null
        }
    }

    suspend fun getTrendingVideos(): List<InvidiousVideo> = withContext(Dispatchers.IO) {
        try {
            val url = "$baseUrl/api/v1/trending?region=US"
            val response = client.newCall(Request.Builder().url(url).build()).execute()
            val videos: List<InvidiousVideo> = gson.fromJson(response.body?.string(), object : TypeToken<List<InvidiousVideo>>() {}.type)
            videos.map { it.copy(thumbnailUrl = "$baseUrl/vi/${it.videoId}/maxresdefault.jpg") }
        } catch (e: Exception) { emptyList() }
    }

    suspend fun searchVideos(query: String): List<InvidiousVideo> = withContext(Dispatchers.IO) {
        try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val url = "$baseUrl/api/v1/search?q=$encodedQuery"
            val response = client.newCall(Request.Builder().url(url).build()).execute()
            val videos: List<InvidiousVideo> = gson.fromJson(response.body?.string(), object : TypeToken<List<InvidiousVideo>>() {}.type)
            videos.map { it.copy(thumbnailUrl = "$baseUrl/vi/${it.videoId}/maxresdefault.jpg") }
        } catch (e: Exception) { emptyList() }
    }

    suspend fun getVideoData(videoId: String): InvidiousVideoData? = withContext(Dispatchers.IO) {
        try {
            val url = "$baseUrl/api/v1/videos/$videoId?local=true"
            val response = client.newCall(Request.Builder().url(url).build()).execute()
            gson.fromJson(response.body?.string(), InvidiousVideoData::class.java)
        } catch (e: Exception) { null }
    }

    suspend fun getStreamUrl(videoId: String, preferredHeight: Int = 720): String? {
        val data = getVideoData(videoId) ?: return null
        val rawUrl = data.formatStreams.firstOrNull { it.qualityLabel.contains("${preferredHeight}p") && it.container == "mp4" }?.url 
            ?: data.formatStreams.firstOrNull { it.container == "mp4" }?.url
            ?: data.formatStreams.firstOrNull()?.url

        return when {
            rawUrl == null -> null
            rawUrl.startsWith("http") -> rawUrl
            rawUrl.startsWith("/") -> "$baseUrl$rawUrl"
            else -> "$baseUrl/$rawUrl"
        }
    }
}

