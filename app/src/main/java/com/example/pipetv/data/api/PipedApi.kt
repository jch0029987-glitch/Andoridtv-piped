package com.example.pipetv.data.api

import com.example.pipetv.data.model.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PipedApi {
    @GET("trending")
    suspend fun getTrending(@Query("region") region: String = "US"): List<PipedVideo>

    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("filter") filter: String = "videos"
    ): SearchResponse

    @GET("streams/{videoId}")
    suspend fun getStream(@Path("videoId") videoId: String): PipedStreamResponse
}
