package com.example.pipetv.data.model

import com.google.gson.annotations.SerializedName

data class PipedVideo(
    @SerializedName("id", alternate = ["videoId"])
    val id: String,
    val title: String?,
    val uploader: String?,
    val thumbnail: String?
)
