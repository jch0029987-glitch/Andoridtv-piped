package com.example.pipetv.data.network

import com.example.pipetv.network.AppDownloader
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.search.SearchInfo
import org.schabi.newpipe.extractor.stream.StreamInfo

class StreamRepository {

    init {
        if (!NewPipe.isInitialized()) {
            NewPipe.init(AppDownloader())
        }
    }

    suspend fun searchVideos(query: String) =
        SearchInfo.getInfo(ServiceList.YouTube, query).items

    suspend fun getVideoInfo(url: String) =
        StreamInfo.getInfo(ServiceList.YouTube, url)
}
