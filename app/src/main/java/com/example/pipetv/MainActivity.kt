package com.example.pipetv

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.material3.* // TV specific components
import coil3.compose.AsyncImage
import com.example.pipetv.data.api.RetrofitClient
import com.example.pipetv.data.model.PipedVideo
import com.example.pipetv.ui.player.VideoPlayerActivity
import com.example.pipetv.ui.theme.PipeTVTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PipeTVTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MainScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var videos by remember { mutableStateOf(emptyList<PipedVideo>()) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Adaptive Grid: 2 columns for phones, 4 for TV/Tablets
    val configuration = LocalConfiguration.current
    val columns = if (configuration.screenWidthDp > 600) 4 else 2

    // Initial Trending Load
    LaunchedEffect(Unit) {
        isLoading = true
        try {
            videos = RetrofitClient.pipedApi.getTrending("US")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        isLoading = false
    }

    Column(modifier = Modifier.padding(16.dp)) {
        // Search Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search YouTube...") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Button(onClick = {
                scope.launch {
                    if (searchQuery.isNotEmpty()) {
                        isLoading = true
                        try {
                            videos = RetrofitClient.pipedApi.search(searchQuery)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Search failed", Toast.LENGTH_SHORT).show()
                        }
                        isLoading = false
                    }
                }
            }) {
                Text("Go")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(videos) { video ->
                    VideoCard(video)
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun VideoCard(video: PipedVideo) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // TV Card handles focus natively, but works with touch on Phone too
    Card(
        onClick = {
            scope.launch {
                try {
                    val videoId = video.id
                    if (videoId.isNotEmpty()) {
                        val streamData = RetrofitClient.pipedApi.getStream(videoId)
                        
                        // Pick HLS for better streaming, fallback to standard MP4
                        val url = streamData.videoStreams.firstOrNull { !it.videoOnly }?.url
                        
                        if (url != null) {
                            val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                                putExtra("video_url", url)
                            }
                            context.startActivity(intent)
                        } else {
                            Toast.makeText(context, "No stream found", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "API Error", Toast.LENGTH_SHORT).show()
                }
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            AsyncImage(
                model = video.thumbnail,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f),
                contentScale = ContentScale.Crop
            )
            androidx.compose.material3.Text(
                text = video.title,
                maxLines = 2,
                modifier = Modifier.padding(8.dp),
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall
            )
        }
    }
}
