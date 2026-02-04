package com.example.pipetv.data.network

import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.downloader.Request
import org.schabi.newpipe.extractor.downloader.Response
import okhttp3.OkHttpClient
import okhttp3.Headers.Companion.toHeaders

class AppDownloader : Downloader() {
    private val client = OkHttpClient.Builder().build()

    override fun execute(request: Request): Response {
        val okRequest = okhttp3.Request.Builder()
            .url(request.url())
            .method(request.httpMethod(), null)
            .headers(request.headers().toHeaders())
            .build()
        
        val response = client.newCall(okRequest).execute()
        return Response(response.code, response.message, response.headers.toMultimap(), response.body?.string(), response.request.url.toString())
    }
}
