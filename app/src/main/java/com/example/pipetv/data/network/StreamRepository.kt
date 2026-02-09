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
            // Initialize NewPipeExtractor once with custom AppDownloader
            NewPipe.init(
                Downloader = AppDownloader(),
                localization = Localization.fromLocale(Locale.ENGLISH),
                contentCountry = ContentCountry("US")
            )
        } catch (e: Exception) {
            // Ignore if already initialized
        }
    }

    /**
     * Search YouTube videos by query
     */
    suspend fun searchVideos(query: String): List<StreamInfoItem> {
        val searchExtractor = ServiceList.YouTube.getSearchExtractor(query)
        searchExtractor.fetchPage()
        return searchExtractor.initialPage.items.filterIsInstance<StreamInfoItem>()
    }

    /**
     * Get full video info, including streams
     */
    suspend fun getVideoInfo(url: String): StreamInfo {
        return StreamInfo.getInfo(ServiceList.YouTube, url)
    }

    /**
     * Optional: fetch playlist items
     */
    suspend fun getPlaylistItems(url: String): List<StreamInfoItem> {
        val playlist = org.schabi.newpipe.extractor.playlist.PlaylistInfo.getInfo(
            ServiceList.YouTube, url
        )
        return playlist.items.filterIsInstance<StreamInfoItem>()
    }
}
