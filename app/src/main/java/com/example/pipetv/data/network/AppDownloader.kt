package com.example.pipetv.data.network

import com.example.pipetv.data.model.PipedVideo
import okhttp3.OkHttpClient
import okhttp3.Request
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AppDownloader {

    private val client = OkHttpClient()
    private val gson = Gson()
    private val BASE_URL = "https://piped.kavin.rocks/api/v1/"

    // THIS is the function MainActivity expects
    fun search(query: String): List<PipedVideo> {
        val url = "${BASE_URL}search?q=$query"
        val request = Request.Builder()
            .url(url)
            .header("User-Agent", "Mozilla/5.0")
            .build()

        val response = client.newCall(request).execute()
        val body = response.body?.string() ?: return emptyList()

        // Parse JSON into list of PipedVideo
        val type = object : TypeToken<List<PipedVideo>>() {}.type
        return gson.fromJson(body, type)
    }
}
