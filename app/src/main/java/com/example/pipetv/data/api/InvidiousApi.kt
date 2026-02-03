package com.example.pipetv.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface InvidiousApi {
    @GET("api/v1/trending")
    suspend fun getTrending(): List<InvidiousVideo>

    @GET("api/v1/search")
    suspend fun search(@Query("q") query: String): List<InvidiousVideo>
}

data class InvidiousVideo(
    val videoId: String,
    val title: String,
    val author: String,
    val videoThumbnails: List<InvidiousThumbnail>
)

data class InvidiousThumbnail(
    val url: String
)
