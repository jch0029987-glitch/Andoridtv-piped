package com.example.pipetv.data.model

data class PipedVideo(
    val id: String, // Ensure this is 'id'
    val title: String,
    val uploader: String, // Changed from uploaderName to uploader
    val thumbnail: String
)
