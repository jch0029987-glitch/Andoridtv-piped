package com.example.pipetv.data.models

data class VideoItem(
    val videoId: String,
    val title: String,
    val thumbnailUrl: String,
    val authorName: String,
    val viewCount: Long
)
