package com.example.pipetv

import android.os.Bundle
import android.os.Process
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Surface
import com.example.pipetv.ui.screens.HomeScreen
import com.example.pipetv.ui.theme.PipeTVTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // PERFORMANCE: Set the thread priority to DISPLAY.
        // This ensures the UI thread gets more CPU cycles than background 
        // image loading or network requests, preventing remote lag.
        Process.setThreadPriority(Process.THREAD_PRIORITY_DISPLAY)

        setContent {
            PipeTVTheme {
                // Surface provides the standard TV background color and handles 
                // basic touch/focus containment for the app.
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = androidx.compose.ui.graphics.RectangleShape,
                    colors = ClickableSurfaceDefaults.colors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                ) {
                    HomeScreen()
                }
            }
        }
    }
}
