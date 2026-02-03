package com.example.pipetv.data.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val PIPED_URL = "https://api.piped.private.coffee/"
    private const val INVIDIOUS_URL = "https://yewtu.be/api/v1/"

    // This is the "Right Way" - A custom interceptor to mimic a browser
    private val headerInterceptor = Interceptor { chain ->
        val request = chain.request().newBuilder()
            .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/141.0.0.0 Safari/537.36")
            .header("Accept", "application/json")
            .build()
        chain.proceed(request)
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(headerInterceptor)
        .build()

    val pipedApi: PipedApi by lazy {
        Retrofit.Builder()
            .baseUrl(PIPED_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PipedApi::class.java)
    }

    val invidiousApi: InvidiousApi by lazy {
        Retrofit.Builder()
            .baseUrl(INVIDIOUS_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(InvidiousApi::class.java)
    }
}
