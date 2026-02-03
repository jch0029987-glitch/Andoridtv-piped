package com.example.pipetv.data.model

import com.google.gson.annotations.SerializedName

data class PipedVideo(
    // Look for all possible ID keys used by various Piped instances
    @SerializedName("id", alternate = ["videoId", "url"])
    val rawId: String?,
    
    val title: String? = null,
    
    @SerializedName("uploader", alternate = ["author", "uploaderName"])
    val uploader: String? = null,
    
    val thumbnail: String? = null
) {
    /**
     * This logic mimics how NewPipe extracts IDs.
     * It handles: 
     * 1. Direct IDs: "abc12345" 
     * 2. Full URLs: "https://www.youtube.com/watch?v=abc12345"
     * 3. Piped Paths: "/watch?v=abc12345"
     */
    val id: String
        get() {
            if (rawId == null) return ""
            return when {
                rawId.contains("v=") -> rawId.substringAfter("v=").substringBefore("&")
                rawId.contains("/") -> rawId.substringAfterLast("/")
                else -> rawId
            }
        }
}
