package com.example.pipetv.data.network

import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.downloader.Request
import org.schabi.newpipe.extractor.downloader.Response
import okhttp3.OkHttpClient
import okhttp3.Headers

class AppDownloader : Downloader() {
    private val client = OkHttpClient.Builder().build()

    override fun execute(request: Request): Response {
        val headersBuilder = Headers.Builder()
        request.headers().forEach { (key, values) ->
            values.forEach { value -> headersBuilder.add(key, value) }
        }

        val okRequest = okhttp3.Request.Builder()
            .url(request.url())
            .method(request.httpMethod(), null)
            .headers(headersBuilder.build())
            .build()
        
        val response = client.newCall(okRequest).execute()
        val body = response.body?.string()
        
        return Response(
            response.code, 
            response.message, 
            response.headers.toMultimap(), 
            body, 
            response.request.url.toString()
        )
    }
}
