package com.example.pipetv.data.models

data class VideoItem(
    val videoId: String,
    val title: String,
    val videoThumbnails: List<Thumbnail>? = null, // Invidious often nests thumbnails
    val author: String,
    val viewCount: Long = 0
) {
    // Helper to get a single URL for AsyncImage
    val thumbnailUrl: String
        get() = videoThumbnails?.firstOrNull()?.url ?: ""
}

data class Thumbnail(
    val url: String,
    val width: Int,
    val height: Int
)
