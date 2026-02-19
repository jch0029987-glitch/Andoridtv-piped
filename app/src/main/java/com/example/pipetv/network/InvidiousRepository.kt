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

    // Stealth Client: Masks as Windows Chrome
    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .build()
            chain.proceed(request)
        }.build()

    suspend fun searchVideos(query: String): List<InvidiousVideo> = withContext(Dispatchers.IO) {
        val url = "$BASE_URL/api/v1/search?q=${query.replace(" ", "+")}"
        val request = Request.Builder().url(url).build()
        
        try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: return@withContext emptyList()
                val type = object : TypeToken<List<InvidiousVideo>>() {}.type
                gson.fromJson(body, type)
            }
        } catch (e: Exception) { emptyList() }
    }

    suspend fun getStreamUrl(videoId: String): String? = withContext(Dispatchers.IO) {
        // ?local=1 is key for hiding usage from carrier (it proxies via your server)
        val url = "$BASE_URL/api/v1/videos/$videoId?local=1"
        val request = Request.Builder().url(url).build()

        try {
            client.newCall(request).execute().use { response ->
                val body = response.body?.string() ?: return@withContext null
                val info = gson.fromJson(body, VideoInfoResponse::class.java)
                
                // Construct the absolute URL for the HLS stream
                info.hlsUrl?.let { 
                    if (it.startsWith("/")) "$BASE_URL$it" else it 
                } ?: info.formatStreams?.maxByOrNull { it.quality }?.url
            }
        } catch (e: Exception) { null }
    }
}
