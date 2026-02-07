package com.example.pipetv.data.network

import com.example.pipetv.data.model.PipedVideo
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder

class AppDownloader {

    private val client = OkHttpClient()
    private val gson = Gson()
    private val BASE_URL = "https://piped.kavin.rocks/api/v1/"

    // Piped API search response wrapper
    data class PipedSearchResponse(
        @SerializedName("items") val items: List<PipedVideo> = emptyList()
    )

    /**
     * Search Piped videos by query
     */
    fun search(query: String): List<PipedVideo> {
        try {
            val encodedQuery = URLEncoder.encode(query, "UTF-8")
            val url = "${BASE_URL}search?q=$encodedQuery"

            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                .build()

            val response = client.newCall(request).execute()
            val body = response.body?.string()
            if (body.isNullOrEmpty()) {
                return emptyList()
            }

            // Optional: log the response for debugging
            println("AppDownloader search response: $body")

            // Parse JSON into PipedSearchResponse
            val searchResponse = gson.fromJson(body, PipedSearchResponse::class.java)
            return searchResponse.items

        } catch (e: Exception) {
            e.printStackTrace()
            return emptyList()
        }
    }
}
