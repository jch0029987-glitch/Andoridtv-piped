package com.example.pipetv

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.pipetv.data.network.AppDownloader
import com.example.pipetv.ui.player.VideoPlayerActivity
import com.example.pipetv.ui.theme.PipeTVTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.localization.ContentCountry
import org.schabi.newpipe.extractor.localization.Localization
import org.schabi.newpipe.extractor.stream.StreamInfo
import org.schabi.newpipe.extractor.stream.StreamInfoItem
import java.util.Locale
import org.schabi.newpipe.extractor.downloader.Downloader

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize NewPipeExtractor with custom downloader
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val scope = rememberCoroutineScope()
    var videos by remember { mutableStateOf(emptyList<StreamInfoItem>()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("Music") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val performSearch: (String) -> Unit = { query ->
        scope.launch(Dispatchers.IO) {
            isLoading = true
            errorMessage = null
            try {
                val service = ServiceList.YouTube
                val search = service.getSearchExtractor(query)
                search.fetchPage()
                val items = search.initialPage.items.filterIsInstance<StreamInfoItem>()

                withContext(Dispatchers.Main) {
                    videos = items
                    isLoading = false
                    if (items.isEmpty()) {
                        errorMessage = "No results found for '$query'"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    isLoading = false
                    errorMessage = "Error: ${e.localizedMessage}"
                    Log.e("PipeTV", "Search error", e)
                }
            }
        }
    }

    // Initial search
    LaunchedEffect(Unit) {
        performSearch(searchQuery)
    }

    Scaffold(
        topBar = {
            Column(Modifier.background(MaterialTheme.colorScheme.surface)) {
                TopAppBar(title = { Text("PipeTV Home") })

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search YouTube...") },
                    trailingIcon = {
                        IconButton(onClick = { performSearch(searchQuery) }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                    },
                    singleLine = true
                )
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize().background(Color.Black)) {
            when {
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                errorMessage != null -> Column(
                    Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(errorMessage!!, color = Color.White, modifier = Modifier.padding(16.dp))
                    Button(onClick = { performSearch(searchQuery) }) {
                        Text("Retry Search")
                    }
                }
                else -> LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(videos) { video -> VideoItem(video) }
                }
            }
        }
    }
}

@Composable
fun VideoItem(video: StreamInfoItem) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable {
                scope.launch(Dispatchers.IO) {
                    try {
                        val info = StreamInfo.getInfo(ServiceList.YouTube, video.url)
                        val streamUrl = info.videoStreams.firstOrNull()?.url ?: info.hlsUrl

                        withContext(Dispatchers.Main) {
                            if (streamUrl != null) {
                                val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                                    putExtra("video_url", streamUrl)
                                }
                                context.startActivity(intent)
                            } else {
                                Toast.makeText(context, "No playable stream found", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("PipeTV", "Playback error", e)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Error fetching video", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column {
            AsyncImage(
                model = video.thumbnails?.firstOrNull()?.url,
                contentDescription = null,
                modifier = Modifier
                    .aspectRatio(16 / 9f)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Column(Modifier.padding(8.dp)) {
                Text(
                    text = video.name ?: "Untitled",
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = video.uploaderName ?: "Unknown",
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}
