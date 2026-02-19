package com.example.pipetv.network

// The single source of truth for your video objects
data class InvidiousVideo(
    val title: String,
    val videoId: String,
    val thumbnailUrl: String = "" // Added default value so .copy() works
)

data class InvidiousVideoData(
    val formatStreams: List<InvidiousStream>
)

data class InvidiousStream(
    val url: String,
    val qualityLabel: String,
    val resolution: String,
    val container: String
)
