package com.example.pipetv.data.model

data class PipedVideo(
    val videoId: String,
    val title: String,
    val uploaderName: String,
    val thumbnail: String
)

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
