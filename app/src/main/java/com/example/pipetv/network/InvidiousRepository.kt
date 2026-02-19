package com.example.pipetv.network

import okhttp3.OkHttpClient
import okhttp3.Request
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InvidiousRepository {
    private val BASE_URL = "http://10.78.240.3:3000"
    private val gson = Gson()
    private val client = OkHttpClient.Builder().build()

    suspend fun searchVideos(query: String): List<InvidiousVideo> = withContext(Dispatchers.IO) {
        val url = "$BASE_URL/api/v1/search?q=$query"
        val request = Request.Builder().url(url).build()
        try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: ""
                val type = object : TypeToken<List<InvidiousVideo>>() {}.type
                gson.fromJson(body, type)
            }
        } catch (e: Exception) { emptyList() }
    }

    suspend fun getStreamUrl(videoId: String): String? = withContext(Dispatchers.IO) {
        val url = "$BASE_URL/api/v1/videos/$videoId?local=1"
        val request = Request.Builder().url(url).build()
        try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: return@withContext null
                val info = gson.fromJson(body, VideoInfoResponse::class.java)
                info.hlsUrl?.let { if (it.startsWith("/")) "$BASE_URL$it" else it }
                    ?: info.formatStreams?.firstOrNull()?.url
            }
        } catch (e: Exception) { null }
    }
}
