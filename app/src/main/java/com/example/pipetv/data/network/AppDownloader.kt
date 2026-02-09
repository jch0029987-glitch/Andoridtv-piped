package com.example.pipetv.data.network

import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaType
import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.downloader.Request
import org.schabi.newpipe.extractor.downloader.Response
import java.net.CookieManager

class AppDownloader : Downloader() {

    private val client = OkHttpClient.Builder()
        .cookieJar(JavaNetCookieJar(CookieManager()))
        .followRedirects(true)
        .followSslRedirects(true)
        .build()

    override fun execute(request: Request): Response {
        val method = request.httpMethod()
        val url = request.url()

        val body = if (method == "POST" || method == "PUT") {
            RequestBody.create("application/json".toMediaType(), ByteArray(0))
        } else null

        // Headers
        val headers = request.headers()?.flatMap { (k, v) -> v.map { k to it } }?.toMap() ?: emptyMap()

        val finalHeaders = headers.toMutableMap().apply {
            put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
            put("Origin", "https://www.youtube.com")
            put("X-YouTube-Client-Name", "1")
            put("X-YouTube-Client-Version", "2.20260204.00.00")
        }

        val okRequest = okhttp3.Request.Builder()
            .url(url)
            .method(method, body)
            .apply { finalHeaders.forEach { (k, v) -> addHeader(k, v) } }
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
