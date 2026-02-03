package com.example.pipetv.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val PIPED_BASE_URL = "https://api.piped.private.coffee/"

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                // Use a standard Desktop User-Agent. Servers trust this more than Mobile ones.
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:122.0) Gecko/20100101 Firefox/122.0")
                .header("Accept", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()

    val pipedApi: PipedApi by lazy {
        Retrofit.Builder()
            .baseUrl(PIPED_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PipedApi::class.java)
    }
}
