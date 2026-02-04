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
        
        // In v0.24.4, the data is typically accessed via getData() 
        // We handle the null case to prevent the "Must have request body" error
        val body = if (method == "POST" || method == "PUT") {
            val rawData = request.data() ?: ByteArray(0)
            RequestBody.create("application/json".toMediaTypeOrNull(), rawData)
        } else {
            null
        }

        val headersBuilder = Headers.Builder()
        // Ensure we don't crash if headers are null
        request.headers()?.forEach { (key, values) ->
            values.forEach { value -> headersBuilder.add(key, value) }
        }

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
