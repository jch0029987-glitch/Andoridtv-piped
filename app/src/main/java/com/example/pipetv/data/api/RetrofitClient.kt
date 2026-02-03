package com.example.pipetv.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // The correct API URL for private.coffee
    private const val PIPED_BASE_URL = "https://api.piped.private.coffee/"
    private const val INVIDIOUS_BASE_URL = "https://inv.tux.rs/"

    private val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "Mozilla/5.0 (Android 14; Mobile; rv:120.0) Gecko/120.0 Firefox/120.0")
                .header("Referer", "https://piped.private.coffee/")
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

    val invidiousApi: InvidiousApi by lazy {
        Retrofit.Builder()
            .baseUrl(INVIDIOUS_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(InvidiousApi::class.java)
    }
}
