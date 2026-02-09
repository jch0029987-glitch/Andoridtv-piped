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
            // Initialize NewPipeExtractor with AppDownloader
            NewPipe.init(
                Downloader = AppDownloader(),
                localization = Localization.fromLocale(Locale.ENGLISH),
                contentCountry = ContentCountry("US")
            )
        } catch (_: Exception) {
            // Already initialized, ignore
        }
    }

    suspend fun searchVideos(query: String): List<StreamInfoItem> {
        val search = ServiceList.YouTube.getSearchExtractor(query)
        search.fetchPage()
        // Fix: some NewPipe versions use .streams instead of .items
        return search.initialPage.streams.filterIsInstance<StreamInfoItem>()
    }

    suspend fun getVideoInfo(url: String): StreamInfo {
        return StreamInfo.getInfo(ServiceList.YouTube, url)
    }
}
