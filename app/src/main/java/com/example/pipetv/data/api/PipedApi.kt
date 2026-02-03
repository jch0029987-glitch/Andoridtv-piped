package com.example.pipetv.data.api

import com.example.pipetv.data.model.*
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PipedApi {
    @GET("trending")
    suspend fun getTrending(@Query("region") region: String): List<PipedVideo>

    @GET("search")
    suspend fun search(
        @Query("q") query: String,
        @Query("filter") filter: String = "videos"
    ): List<PipedVideo>

    @GET("streams/{videoId}")
    suspend fun getStream(@Path("videoId") videoId: String): PipedStreamResponse
}

interface InvidiousApi {
    @GET("api/v1/trending")
    suspend fun getTrending(): List<InvidiousVideo>

    @GET("api/v1/search")
    suspend fun search(@Query("q") query: String): List<InvidiousVideo>
}
