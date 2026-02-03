package com.example.pipetv.data.api

import com.example.pipetv.data.model.PipedVideo
import com.example.pipetv.data.model.PipedStreamResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PipedApi {
    @GET("trending")
    suspend fun getTrending(@Query("region") region: String): List<PipedVideo>

    // Search returns an object, so we use PipedSearchResponse
    @GET("search")
    suspend fun search(@Query("q") query: String): PipedSearchResponse

    @GET("streams/{id}")
    suspend fun getStream(@Path("id") id: String): PipedStreamResponse
}

// Wrapper to handle the "BEGIN_OBJECT" search result
data class PipedSearchResponse(
    val items: List<PipedVideo>
)
