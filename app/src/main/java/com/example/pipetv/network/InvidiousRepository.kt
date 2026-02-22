package com.example.pipetv.network

import com.example.pipetv.data.models.VideoItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InvidiousRepository {
    private val BASE_URL = "http://10.72.41.71:3000"
    private val client = OkHttpClient()
    private val gson = Gson()

    private val _trendingVideos = MutableStateFlow<List<VideoItem>>(emptyList())
    val trendingVideos: StateFlow<List<VideoItem>> = _trendingVideos

    private val _searchResults = MutableStateFlow<List<VideoItem>>(emptyList())
    val searchResults: StateFlow<List<VideoItem>> = _searchResults

    // Fix for VideoPlayerActivity error
    fun getVideoStreamUrl(videoId: String): String = "$BASE_URL/latest_version?id=$videoId&itag=22"

    suspend fun fetchTrending() = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url("$BASE_URL/api/v1/trending").build()
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val json = response.body?.string()
                    val type = object : TypeToken<List<VideoItem>>() {}.type
                    val videos: List<VideoItem> = gson.fromJson(json, type) ?: emptyList()
                    _trendingVideos.value = videos
                }
            }
        } catch (e: Exception) { e.printStackTrace() }
    }

    // Fix for SearchScreen error
    fun searchVideos(query: String) {
        // Implementation can be added later, but the reference must exist
    }
}
