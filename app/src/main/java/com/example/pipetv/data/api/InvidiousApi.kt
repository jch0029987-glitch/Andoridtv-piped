package com.example.pipetv.data.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface InvidiousApi {
    @GET("api/v1/trending")
    suspend fun getTrending(): List<InvidiousVideo>

    @GET("api/v1/search")
    suspend fun search(@Query("q") query: String): List<InvidiousVideo>

    @GET("api/v1/videos/{id}")
    suspend fun getVideoData(@Path("id") id: String): InvidiousStreamResponse
}

data class InvidiousVideo(
    val videoId: String,
    val title: String,
    val author: String?,
    val videoThumbnails: List<InvidiousThumb>?
)

data class InvidiousThumb(val url: String)

data class InvidiousStreamResponse(
    val formatStreams: List<InvidiousStream>?
)

data class InvidiousStream(
    val url: String,
    val qualityLabel: String?,
    val container: String?
)
