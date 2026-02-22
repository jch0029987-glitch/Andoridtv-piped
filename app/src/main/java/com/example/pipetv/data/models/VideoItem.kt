package com.example.pipetv.data.models

import com.google.gson.annotations.SerializedName

data class VideoItem(
    @SerializedName("videoId", alternate = ["id"])
    val videoId: String,
    val title: String,
    val videoThumbnails: List<Thumbnail>? = null,
    val author: String,
    val viewCount: Long = 0
) {
    val thumbnailUrl: String
        get() = videoThumbnails?.firstOrNull()?.url ?: ""
}

data class Thumbnail(
    val url: String,
    val width: Int,
    val height: Int
)
