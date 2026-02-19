package com.example.pipetv

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.pipetv.network.InvidiousRepository
import com.example.pipetv.network.InvidiousVideo
import com.example.pipetv.ui.player.VideoPlayerActivity
import com.example.pipetv.ui.theme.PipeTVTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val repository = InvidiousRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PipeTVTheme {
                MainScreen(repository)
            }
        }
    }
}

@Composable
fun MainScreen(repository: InvidiousRepository) {
    val scope = rememberCoroutineScope()
    var videos by remember { mutableStateOf(emptyList<InvidiousVideo>()) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current

    // Initial load: Fetch trending or a default search from your instance
    LaunchedEffect(Unit) {
        videos = repository.searchVideos("trending")
        isLoading = false
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Local Invidious TV") })
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize().background(Color.Black)) {
            if (isLoading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(videos) { video ->
                        VideoItem(video) {
                            scope.launch {
                                // Forces local proxying via your 10.78.240.3 server
                                val streamUrl = repository.getStreamUrl(video.videoId)
                                if (streamUrl != null) {
                                    val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                                        putExtra("video_url", streamUrl)
                                    }
                                    context.startActivity(intent)
                                } else {
                                    Toast.makeText(context, "Instance error", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VideoItem(video: InvidiousVideo, onClick: () -> Unit) {
    Card(
        modifier = Modifier.padding(8.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
    ) {
        Column {
            AsyncImage(
                model = video.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier.aspectRatio(16/9f).fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Text(
                text = video.title,
                modifier = Modifier.padding(8.dp),
                color = Color.White,
                maxLines = 2
            )
        }
    }
}
