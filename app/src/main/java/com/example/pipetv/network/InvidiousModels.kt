package com.example.pipetv.network

import com.google.gson.annotations.SerializedName

data class InvidiousVideo(
    val title: String,
    val videoId: String,
    val author: String,
    @SerializedName("videoThumbnails") val thumbnails: List<InvidiousThumbnail>?
) {
    val thumbnailUrl: String 
        get() = thumbnails?.find { it.quality == "medium" }?.url ?: "https://img.youtube.com/vi/$videoId/mqdefault.jpg"
}

data class InvidiousThumbnail(val url: String, val quality: String)

data class VideoSource(val url: String, val quality: String)
data class VideoInfoResponse(
    val formatStreams: List<VideoSource>?,
    val hlsUrl: String?
)
