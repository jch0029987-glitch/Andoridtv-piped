package com.example.pipetv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.lazy.grid.*
import androidx.tv.material3.*
import coil3.compose.AsyncImage
import com.example.pipetv.data.api.RetrofitClient
import com.example.pipetv.data.model.PipedVideo
import com.example.pipetv.ui.theme.PipeTVTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PipeTVTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TrendingScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TrendingScreen() {
    val scope = rememberCoroutineScope()
    var videos by remember { mutableStateOf(emptyList<PipedVideo>()) }

    // Fetch data from private.coffee on launch
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                videos = RetrofitClient.api.getTrending("US")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Trending on Piped",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        TvLazyVerticalGrid(
            columns = TvGridCells.Fixed(4), // 4 videos per row on TV
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(videos) { video ->
                VideoCard(video)
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun VideoCard(video: PipedVideo) {
    // Standard TV card with focus support
    StandardCard(
        onClick = { /* TODO: Open Player */ },
        imageProvider = {
            AsyncImage(
                model = video.thumbnail,
                contentDescription = video.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.aspectRatio(16f / 9f)
            )
        },
        title = {
            Text(
                text = video.title,
                maxLines = 2,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        subtitle = {
            Text(
                text = video.uploaderName,
                style = MaterialTheme.typography.bodySmall
            )
        },
        modifier = Modifier.width(200.dp)
    )
}
