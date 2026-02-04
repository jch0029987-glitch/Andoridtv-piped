package com.example.pipetv.data.network

import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.downloader.Request
import org.schabi.newpipe.extractor.downloader.Response
import okhttp3.OkHttpClient
import okhttp3.Headers
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull

class AppDownloader : Downloader() {
    private val client = OkHttpClient.Builder().build()

    override fun execute(request: Request): Response {
        val method = request.httpMethod()
        val url = request.url()

        // Fix: POST/PUT requests must have a body in OkHttp.
        // We pass an empty body if none is provided to satisfy the library.
        val body = if (method == "POST" || method == "PUT") {
            RequestBody.create("application/json".toMediaTypeOrNull(), ByteArray(0))
        } else {
            null
        }

        val headersBuilder = Headers.Builder()
        request.headers()?.forEach { (key, values) ->
            values.forEach { value -> headersBuilder.add(key, value) }
        }

        // Add a modern User-Agent to prevent YouTube from blocking the "Trending" scraper
        headersBuilder.set("User-Agent", "Mozilla/5.0 (Android 16; Mobile; rv:135.0) Gecko/135.0 Firefox/135.0")

        val okRequest = okhttp3.Request.Builder()
            .url(url)
            .method(method, body)
            .headers(headersBuilder.build())
            .build()
        
        val response = client.newCall(okRequest).execute()
        val responseBody = response.body?.string()
        
        return Response(
            response.code, 
            response.message, 
            response.headers.toMultimap(), 
            responseBody, 
            response.request.url.toString()
        )
    }
}
