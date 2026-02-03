package com.example.pipetv.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // As requested: the private.coffee instance
    private const val BASE_URL = "https://api.piped.private.coffee/"

    val api: PipedApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PipedApi::class.java)
    }
}
