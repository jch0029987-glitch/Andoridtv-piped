package com.example.pipetv.data.network

import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.downloader.Request
import org.schabi.newpipe.extractor.downloader.Response
import java.net.CookieManager
import java.util.concurrent.TimeUnit

class AppDownloader : Downloader() {

    private val client = OkHttpClient.Builder()
        .cookieJar(JavaNetCookieJar(CookieManager()))
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    override fun execute(request: Request): Response {
        val body = request.dataToSend()?.let {
            RequestBody.create(
                request.contentType()?.toMediaType(),
                it
            )
        }

        val headers = Headers.Builder().apply {
            request.headers()?.forEach { (key, values) ->
                values.forEach { add(key, value) }
            }

            // Stable YouTube Android / TV fingerprint
            set("User-Agent", "com.google.android.youtube.tv/1.0")
            set("X-YouTube-Client-Name", "3")
            set("X-YouTube-Client-Version", "17.31.35")
            set("Origin", "https://www.youtube.com")
        }.build()

        val okRequest = okhttp3.Request.Builder()
            .url(request.url())
            .method(request.httpMethod(), body)
            .headers(headers)
            .build()

        client.newCall(okRequest).execute().use { response ->
            return Response(
                response.code,
                response.message,
                response.headers.toMultimap(),
                response.body?.string(),
                response.request.url.toString()
            )
        }
    }
}
