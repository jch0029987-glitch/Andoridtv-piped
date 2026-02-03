package com.example.pipetv.data.model

import com.google.gson.annotations.SerializedName

// Model for video items in Trending/Search results
data class PipedVideo(
    val title: String,
    val url: String, // e.g., "/watch?v=dQw4w9WgXcQ"
    val thumbnail: String,
    val uploaderName: String,
    val uploaderAvatar: String?,
    val views: Long,
    val uploadedDate: String?
) {
    val videoId: String get() = url.split("=").last()
}

// Model for the stream response
data class PipedStreamResponse(
    val hls: String?,
    @SerializedName("videoStreams") val videoStreams: List<VideoStream>?,
    val audioStreams: List<AudioStream>?,
    val title: String?,
    val description: String?
)

data class VideoStream(
    val url: String,
    val format: String,
    val quality: String,
    val videoOnly: Boolean
)

data class AudioStream(
    val url: String,
    val format: String,
    val bitrate: Int
)

data class SearchResponse(
    val items: List<PipedVideo>
)
