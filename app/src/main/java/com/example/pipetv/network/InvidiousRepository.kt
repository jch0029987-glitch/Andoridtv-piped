package com.example.pipetv.network

import android.util.Log
import com.example.pipetv.data.models.VideoItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InvidiousRepository {
    // Public so the Player Activity can access it for building stream URLs
    val BASE_URL = "http://10.72.41.71:3000"
    private val client = OkHttpClient()
    private val gson = Gson()

    private val _trendingVideos = MutableStateFlow<List<VideoItem>>(emptyList())
    val trendingVideos: StateFlow<List<VideoItem>> = _trendingVideos

    suspend fun fetchTrending() = withContext(Dispatchers.IO) {
        try {
            val url = "$BASE_URL/api/v1/trending"
            val request = Request.Builder().url(url).build()
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val json = response.body?.string()
                    val type = object : TypeToken<List<VideoItem>>() {}.type
                    val videos: List<VideoItem> = gson.fromJson(json, type) ?: emptyList()
                    _trendingVideos.emit(videos)
                    Log.d("PipeTV", "Successfully loaded ${videos.size} videos")
                }
            }
        } catch (e: Exception) {
            Log.e("PipeTV", "Repository Error: ${e.message}")
        }
    }

    fun searchVideos(query: String) { /* Optional: Implement Search API call here */ }
}
