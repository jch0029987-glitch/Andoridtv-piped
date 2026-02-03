package com.example.pipetv

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.pipetv.data.api.RetrofitClient
import com.example.pipetv.data.model.PipedVideo
import com.example.pipetv.ui.player.VideoPlayerActivity
import com.example.pipetv.ui.theme.PipeTVTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { PipeTVTheme { MainScreen() } }
    }
}

@Composable
fun MainScreen() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var videos by remember { mutableStateOf(emptyList<PipedVideo>()) }
    var isLoading by remember { mutableStateOf(false) }

    val fetchData = {
        scope.launch {
            isLoading = true
            try {
                videos = RetrofitClient.pipedApi.getTrending("US")
            } catch (e: Exception) {
                Log.e("PIPED", "Fetch error", e)
                Toast.makeText(context, "Server Error: 500", Toast.LENGTH_SHORT).show()
            }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) { fetchData() }

    Scaffold { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(videos) { video ->
                    VideoCard(video)
                }
            }
            if (isLoading) CircularProgressIndicator(Modifier.align(Alignment.Center))
        }
    }
}

@Composable
fun VideoCard(video: PipedVideo) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Card(Modifier.clickable {
        scope.launch {
            try {
                val streamData = RetrofitClient.pipedApi.getStream(video.id)
                // Browser-style priority: HLS first, then high quality video
                val url = streamData.hls ?: streamData.videoStreams?.firstOrNull { !it.videoOnly }?.url
                
                if (url != null) {
                    val intent = Intent(context, VideoPlayerActivity::class.java)
                    intent.putExtra("video_url", url)
                    context.startActivity(intent)
                }
            } catch (e: Exception) {
                Log.e("PIPED", "Stream error", e)
                Toast.makeText(context, "Stream Error 500", Toast.LENGTH_SHORT).show()
            }
        }
    }) {
        Column {
            AsyncImage(model = video.thumbnail, contentDescription = null, Modifier.aspectRatio(16/9f), contentScale = ContentScale.Crop)
            Text(video.title ?: "", Modifier.padding(8.dp), maxLines = 2)
        }
    }
}
