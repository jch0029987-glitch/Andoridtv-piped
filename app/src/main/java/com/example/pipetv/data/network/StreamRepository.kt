package com.example.pipetv.data.network

import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.localization.ContentCountry
import org.schabi.newpipe.extractor.localization.Localization
import org.schabi.newpipe.extractor.search.SearchInfo
import org.schabi.newpipe.extractor.stream.StreamInfo
import org.schabi.newpipe.extractor.stream.StreamInfoItem
import java.util.Locale

class StreamRepository {

    init {
        NewPipe.init(
            AppDownloader(),
            Localization.fromLocale(Locale.US),
            ContentCountry("US")
        )
    }

    suspend fun searchVideos(query: String): List<StreamInfoItem> {
        val searchInfo = SearchInfo.getInfo(
            ServiceList.YouTube,
            query,
            listOf("videos"),
            null
        )
        return searchInfo.relatedItems.filterIsInstance<StreamInfoItem>()
    }

    suspend fun getVideoInfo(url: String): StreamInfo {
        return StreamInfo.getInfo(ServiceList.YouTube, url)
    }
}
