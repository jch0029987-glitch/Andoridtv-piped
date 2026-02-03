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

    // This is the call that fails if the ID is not passed correctly
    @GET("streams/{id}")
    suspend fun getStream(@Path("id") id: String): PipedStreamResponse
}

/**
 * Piped's search endpoint returns an object containing an "items" list,
 * unlike the trending endpoint which returns a direct list.
 */
data class PipedSearchResponse(
    val items: List<PipedVideo>? = emptyList()
)

/**
 * Data structure for the stream response. 
 * We use nullable types to prevent crashes if certain fields are missing.
 */
data class PipedStreamResponse(
    val videoStreams: List<PipedStream>? = emptyList(),
    val audioStreams: List<PipedStream>? = emptyList(),
    val title: String? = null,
    val description: String? = null
)

data class PipedStream(
    val url: String? = null,
    val format: String? = null,
    val quality: String? = null,
    val videoOnly: Boolean = false
)
