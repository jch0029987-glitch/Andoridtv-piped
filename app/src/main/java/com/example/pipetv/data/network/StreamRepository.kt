package com.example.pipetv.network

import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.localization.ContentCountry
import org.schabi.newpipe.extractor.localization.Localization
import org.schabi.newpipe.extractor.stream.StreamInfo
import org.schabi.newpipe.extractor.stream.StreamInfoItem
import java.util.Locale

class StreamRepository {

    init {
        try {
            NewPipe.init(
                downloader = AppDownloader(),
                localization = Localization.fromLocale(Locale.ENGLISH),
                contentCountry = ContentCountry("US")
            )
        } catch (_: Exception) {
            // Already initialized
        }
    }

    suspend fun searchVideos(query: String): List<StreamInfoItem> {
        val search = ServiceList.YouTube.getSearchExtractor(query)
        search.fetchPage()
        return search.initialPage.items.filterIsInstance<StreamInfoItem>()
    }

    suspend fun getVideoInfo(url: String): StreamInfo {
        return StreamInfo.getInfo(ServiceList.YouTube, url)
    }
}
