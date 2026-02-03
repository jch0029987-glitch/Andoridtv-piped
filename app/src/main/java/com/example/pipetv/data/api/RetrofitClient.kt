package com.example.pipetv.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Standard public instances for Piped and Invidious
    private const val PIPED_BASE_URL = "https://pipedapi.kavin.rocks/"
    private const val INVIDIOUS_BASE_URL = "https://inv.tux.rs/"

    val pipedApi: PipedApi by lazy {
        Retrofit.Builder()
            .baseUrl(PIPED_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PipedApi::class.java)
    }

    val invidiousApi: InvidiousApi by lazy {
        Retrofit.Builder()
            .baseUrl(INVIDIOUS_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(InvidiousApi::class.java)
    }
}
