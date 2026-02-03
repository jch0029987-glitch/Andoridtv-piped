package com.example.pipetv.data.model

import com.google.gson.annotations.SerializedName

data class PipedVideo(
    @SerializedName("id", alternate = ["videoId", "url"])
    private val rawId: String?,
    val title: String? = null,
    val uploader: String? = null,
    @SerializedName("thumbnail", alternate = ["thumbnailUrl"])
    val thumbnail: String? = null
) {
    val id: String
        get() {
            val idStr = rawId ?: return ""
            return when {
                idStr.contains("v=") -> idStr.substringAfter("v=").substringBefore("&")
                idStr.contains("/") -> idStr.substringAfterLast("/")
                else -> idStr
            }.trim()
        }
}
