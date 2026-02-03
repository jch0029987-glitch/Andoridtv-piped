package com.example.pipetv

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface as TvSurface
import coil3.compose.AsyncImage
import com.example.pipetv.data.api.RetrofitClient
import com.example.pipetv.data.model.PipedVideo
import com.example.pipetv.ui.player.VideoPlayerActivity
import com.example.pipetv.ui.theme.PipeTVTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PipeTVTheme {
                MainScreen()
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var videos by remember { mutableStateOf(emptyList<PipedVideo>()) }
    var isLoading by remember { mutableStateOf(false) }
    var selectedSource by remember { mutableIntStateOf(0) } // 0: Piped, 1: Invidious
    
    val scope = rememberCoroutineScope()
    val config = LocalConfiguration.current
    val isTv = config.screenWidthDp > 900 // Simple heuristic for TV/Tablet
    val columns = if (isTv) 4 else 2

    // Logic to fetch videos
    val performSearch = {
        scope.launch {
            isLoading = true
            try {
                videos = if (selectedSource == 0) {
                    if (searchQuery.isEmpty()) RetrofitClient.pipedApi.getTrending("US")
                    else RetrofitClient.pipedApi.search(searchQuery)
                } else {
                    val inv = if (searchQuery.isEmpty()) RetrofitClient.invidiousApi.getTrending()
                    else RetrofitClient.invidiousApi.search(searchQuery)
                    inv.map { PipedVideo(it.videoId, null, it.title, it.author, it.videoThumbnails[0].url) }
                }
            } catch (e: Exception) { e.printStackTrace() }
            isLoading = false
        }
    }

    LaunchedEffect(selectedSource) { performSearch() }

    Scaffold(
        topBar = {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("Search...") },
                        singleLine = true
                    )
                    Spacer(Modifier.width(8.dp))
                    Button(onClick = { performSearch() }) { Text("Go") }
                }
                
                Spacer(Modifier.height(8.dp))

                // Modern Compose Segmented Buttons
                SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                    SegmentedButton(
                        selected = selectedSource == 0,
                        onClick = { selectedSource = 0 },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2),
                        label = { Text("Piped") }
                    )
                    SegmentedButton(
                        selected = selectedSource == 1,
                        onClick = { selectedSource = 1 },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2),
                        label = { Text("Invidious") }
                    )
                }
            }
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier.padding(padding).padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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

    // Using TV Card for both because it handles D-Pad focus beautifully
    // and works perfectly with touch.
    androidx.tv.material3.Card(
        onClick = {
            scope.launch {
                try {
                    val streamData = RetrofitClient.pipedApi.getStream(video.id)
                    val url = streamData.videoStreams.firstOrNull { !it.videoOnly }?.url
                    if (url != null) {
                        val intent = Intent(context, VideoPlayerActivity::class.java)
                        intent.putExtra("video_url", url)
                        context.startActivity(intent)
                    }
                } catch (e: Exception) { e.printStackTrace() }
            }
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            AsyncImage(
                model = video.thumbnail,
                contentDescription = null,
                modifier = Modifier.aspectRatio(16f/9f),
                contentScale = ContentScale.Crop
            )
            Text(
                text = video.title,
                modifier = Modifier.padding(8.dp),
                maxLines = 2,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
