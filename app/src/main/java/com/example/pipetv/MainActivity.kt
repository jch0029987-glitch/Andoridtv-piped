package com.example.pipetv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.tv.material3.*
import com.example.pipetv.ui.theme.PipeTVTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PipeTVTheme {
                Surface(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    shape = ClickableSurfaceDefaults.shape()
                ) {
                    // This is where our Screen Navigation will live
                    Text("Welcome to PipeTV on private.coffee!")
                }
            }
        }
    }
}
