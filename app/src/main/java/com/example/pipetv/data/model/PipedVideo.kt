package com.example.pipetv.data.model

import com.google.gson.annotations.SerializedName

data class PipedVideo(
    // This tells Gson to accept "id" OR "videoId" from the JSON
    @SerializedName("id", alternate = ["videoId"])
    val id: String,
    
    val title: String? = null,
    
    @SerializedName("uploader", alternate = ["author", "uploaderName"])
    val uploader: String? = null,
    
    val thumbnail: String? = null
)
