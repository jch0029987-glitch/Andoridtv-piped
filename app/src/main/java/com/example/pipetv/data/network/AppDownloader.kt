package com.example.pipetv.data.network

import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.downloader.Request
import org.schabi.newpipe.extractor.downloader.Response
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Headers

class AppDownloader : Downloader() {

    private val client = OkHttpClient.Builder().build()

    override fun execute(request: Request): Response {
        val method = request.httpMethod()
        val url = request.url()
        val body = if (method == "POST" || method == "PUT") {
            RequestBody.create("application/json".toMediaTypeOrNull(), ByteArray(0))
        } else null

        val headersBuilder = Headers.Builder()
        request.headers()?.forEach { (key, values) ->
            values.forEach { value -> headersBuilder.add(key, value) }
        }

        // Required headers to prevent YouTube blocking
        headersBuilder.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
        headersBuilder.set("X-YouTube-Client-Name", "1")
        headersBuilder.set("X-YouTube-Client-Version", "2.20260204.00.00")
        headersBuilder.set("Origin", "https://www.youtube.com")

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
