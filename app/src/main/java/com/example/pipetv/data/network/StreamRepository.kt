package com.example.pipetv.data.network

import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.stream.StreamInfo
import org.schabi.newpipe.extractor.stream.StreamInfoItem

class StreamRepository {

    suspend fun searchVideos(query: String): List<StreamInfoItem> {
        val searchExtractor = ServiceList.YouTube.getSearchExtractor(query)
        searchExtractor.fetchPage()
        return searchExtractor.initialPage.items
            .filterIsInstance<StreamInfoItem>()
    }

    suspend fun getVideoInfo(url: String): StreamInfo {
        return StreamInfo.getInfo(ServiceList.YouTube, url)
    }
}
