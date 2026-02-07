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

    // List of public Invidious instances to try
    private val instances = listOf(
        "https://yewtu.be",
        "https://inv.nadeko.net",
        "https://invidious.nerdvpn.de"
    )

    // API response wrapper
    data class InvidiousSearchResponse(
        @SerializedName("items") val items: List<InvidiousItem>? = emptyList()
    )

    data class InvidiousItem(
        val type: String?,
        val videoId: String?,
        val title: String?,
        val author: String?,
        @SerializedName("videoThumbnails") val thumbnails: List<Thumbnail>?
    )

    data class Thumbnail(
        val url: String?
    )

    /**
     * Search videos using Invidious instances
     */
    fun search(query: String): List<PipedVideo> {
        val encodedQuery = URLEncoder.encode(query, "UTF-8")

        for (instance in instances) {
            try {
                val url = "$instance/api/v1/search?q=$encodedQuery&type=video"
                val request = Request.Builder()
                    .url(url)
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .build()

                val response = client.newCall(request).execute()
                val body = response.body?.string() ?: continue

                // If response contains error or CAPTCHA, try next instance
                if (body.contains("error", ignoreCase = true) || body.contains("captcha", ignoreCase = true)) {
                    continue
                }

                // Parse JSON
                val searchResponse = gson.fromJson(body, InvidiousSearchResponse::class.java)
                val items = searchResponse.items ?: continue

                // Map to PipedVideo
                val videos = items.filter { it.type == "video" }.map {
                    PipedVideo(
                        rawId = it.videoId,
                        title = it.title,
                        uploader = it.author,
                        thumbnail = it.thumbnails?.firstOrNull()?.url
                    )
                }

                if (videos.isNotEmpty()) return videos

            } catch (e: Exception) {
                e.printStackTrace()
                // Try next instance
                continue
            }
        }

        // No instance returned results
        return emptyList()
    }
}
