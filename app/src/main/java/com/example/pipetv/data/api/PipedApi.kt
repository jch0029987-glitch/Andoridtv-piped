package com.example.pipetv.data.api

import com.example.pipetv.data.model.PipedVideo
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PipedApi {
    @GET("trending")
    suspend fun getTrending(@Query("region") region: String): List<PipedVideo>

    @GET("search")
    suspend fun search(@Query("q") query: String): PipedSearchResponse

    @GET("streams/{id}")
    suspend fun getStream(@Path("id") id: String): PipedStreamResponse
}

data class PipedSearchResponse(
    val items: List<PipedVideo>? = emptyList()
)

data class PipedStreamResponse(
    val videoStreams: List<PipedStream>? = emptyList()
)

data class PipedStream(
    val url: String? = null,
    val videoOnly: Boolean = false
)
