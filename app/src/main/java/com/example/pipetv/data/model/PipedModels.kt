package com.example.pipetv.data.model

// Standard Video model for both Piped and Invidious
data class PipedVideo(
    val videoId: String? = null,
    val url: String? = null,
    val title: String,
    val uploaderName: String,
    val thumbnail: String
) {
    // This helper ensures we always have an ID, even if the API only sends a URL
    val id: String get() = videoId ?: url?.split("v=")?.last() ?: ""
}

data class PipedStreamResponse(
    val videoStreams: List<VideoStream>
)

data class VideoStream(
    val url: String,
    val quality: String,
    val videoOnly: Boolean
)

data class InvidiousVideo(
    val videoId: String,
    val title: String,
    val author: String,
    val videoThumbnails: List<InvidiousThumb>
)

data class InvidiousThumb(val url: String)
