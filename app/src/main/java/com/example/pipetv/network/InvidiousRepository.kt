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

    // Set your GitHub details here
    private val GITHUB_RELEASE_URL = "https://api.github.com/repos/YOUR_USER/Andoridtv-piped/releases/latest"

    suspend fun checkForUpdates(): Pair<String, String>? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url(GITHUB_RELEASE_URL)
                .header("Accept", "application/vnd.github+json")
                .build()
            
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext null

            val releaseMap: Map<String, Any> = gson.fromJson(response.body?.string(), object : TypeToken<Map<String, Any>>() {}.type)
            val tagName = releaseMap["tag_name"] as? String ?: return@withContext null
            
            val assets = releaseMap["assets"] as? List<Map<String, Any>>
            val apkUrl = assets?.firstOrNull { (it["name"] as? String)?.endsWith(".apk") == true }?.get("browser_download_url") as? String

            if (apkUrl != null) Pair(tagName, apkUrl) else null
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

    suspend fun getStreamUrl(videoId: String): String? = withContext(Dispatchers.IO) {
        try {
            val url = "$baseUrl/api/v1/videos/$videoId?local=true"
            val response = client.newCall(Request.Builder().url(url).build()).execute()
            val data = gson.fromJson(response.body?.string(), InvidiousVideoData::class.java)
            
            val stream = data.formatStreams.firstOrNull { it.container == "mp4" }?.url ?: data.formatStreams.firstOrNull()?.url
            if (stream == null) return@withContext null
            
            if (stream.startsWith("http")) stream else "$baseUrl$stream"
        } catch (e: Exception) { null }
    }
}
