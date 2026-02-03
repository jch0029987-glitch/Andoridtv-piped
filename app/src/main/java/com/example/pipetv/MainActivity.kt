package com.example.pipetv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.tv.material3.* // TV-specific Material3
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

        // Standardized grid for 2026
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
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
    // TV-optimized Card from androidx.tv.material3
    Card(
        onClick = { /* TODO: Open Player */ },
        modifier = Modifier.width(200.dp)
    ) {
        Column {
            AsyncImage(
                model = video.thumbnail,
                contentDescription = video.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.aspectRatio(16f / 9f)
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = video.title,
                    maxLines = 2,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = video.uploaderName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
