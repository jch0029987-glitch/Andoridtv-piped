package com.example.pipetv.network  // Make sure this matches AppDownloader

import com.example.pipetv.network.AppDownloader
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
                Downloader = AppDownloader(),
                localization = Localization.fromLocale(Locale.ENGLISH),
                contentCountry = ContentCountry("US")
            )
        } catch (_: Exception) { }
    }

    suspend fun searchVideos(query: String): List<StreamInfoItem> {
        val search = ServiceList.YouTube.getSearchExtractor(query)
        search.fetchPage()
        return search.initialPage.items.filterIsInstance<StreamInfoItem>() // Use 'items'
    }

    suspend fun getVideoInfo(url: String): StreamInfo {
        return StreamInfo.getInfo(ServiceList.YouTube, url)
    }
}
