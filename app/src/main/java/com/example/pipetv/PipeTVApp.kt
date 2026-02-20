package com.example.pipetv

import android.app.Application
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.PlatformContext
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import okio.Path.Companion.toPath
import okhttp3.OkHttpClient

class PipeTVApp : Application(), SingletonImageLoader.Factory {

    // Masking: Makes traffic look like a desktop browser to bypass PDAnet/Carrier detection
    private val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36"
    private val selfHostedReferer = "http://10.78.240.3/"

    val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", userAgent)
                    .header("Referer", selfHostedReferer)
                    .build()
                chain.proceed(request)
            }
            .build()
    }

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .memoryCache { MemoryCache.Builder().maxSizePercent(context, 0.2).build() }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.absolutePath.toPath())
                    .maxSizeBytes(100 * 1024 * 1024)
                    .build()
            }
            .build()
    }
}
