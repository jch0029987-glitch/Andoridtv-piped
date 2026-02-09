package com.example.pipetv

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.pipetv.data.network.AppDownloader
import com.example.pipetv.ui.theme.PipeTVTheme
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.localization.ContentCountry
import org.schabi.newpipe.extractor.localization.Localization
import java.util.Locale

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize NewPipeExtractor
        try {
            NewPipe.init(
                AppDownloader(),
                Localization.fromLocale(Locale.ENGLISH),
                ContentCountry("US")
            )
        } catch (e: Exception) {
            Log.e("PipeTV", "NewPipe init failed", e)
        }

        setContent {
            PipeTVTheme {
                MainScreen()
            }
        }
    }
}
