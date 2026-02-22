package com.example.pipetv.network

import com.example.pipetv.data.models.VideoItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log

class InvidiousRepository {
    private val BASE_URL = "http://10.72.41.71:3000"
    private val client = OkHttpClient()
    private val gson = Gson()

    private val _trendingVideos = MutableStateFlow<List<VideoItem>>(emptyList())
    val trendingVideos: StateFlow<List<VideoItem>> = _trendingVideos

    suspend fun fetchTrending() {
        withContext(Dispatchers.IO) {
            try {
                val request = Request.Builder().url("$BASE_URL/api/v1/trending").build()
                val response = client.newCall(request).execute()
                
                if (response.isSuccessful) {
                    val json = response.body?.string()
                    val type = object : TypeToken<List<VideoItem>>() {}.type
                    val list = gson.fromJson<List<VideoItem>>(json, type) ?: emptyList()
                    
                    // Push to the UI thread safely
                    _trendingVideos.emit(list)
                }
            } catch (e: Exception) {
                Log.e("PipeTV", "Network error: \${e.message}")
            }
        }
    }
}
