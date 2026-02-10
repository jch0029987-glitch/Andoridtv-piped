package com.example.pipetv

import android.app.Application
import com.example.pipetv.network.AppDownloader
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.localization.ContentCountry
import org.schabi.newpipe.extractor.localization.Localization
import java.util.Locale

class PipeTVApp : Application() {

    override fun onCreate() {
        super.onCreate()

        NewPipe.init(
            AppDownloader(),
            Localization.fromLocale(Locale.US),
            ContentCountry("US")
        )
    }
}
