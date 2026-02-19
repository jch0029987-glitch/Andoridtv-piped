package com.example.pipetv

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(repository: InvidiousRepository) {
    val scope = rememberCoroutineScope()
    var videos by remember { mutableStateOf(emptyList<InvidiousVideo>()) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf("Trending") }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    val categories = listOf("Trending", "Music", "Gaming", "Movies")
    val searchFocusRequester = remember { FocusRequester() }

    LaunchedEffect(selectedCategory, searchQuery) {
        isLoading = true
        val query = if (isSearchActive && searchQuery.isNotEmpty()) searchQuery else selectedCategory
        videos = repository.searchVideos(query)
        isLoading = false
    }

    Row(Modifier.fillMaxSize().background(Color.Black)) {
        // --- TV SIDEBAR ---
        Column(
            Modifier
                .fillMaxHeight()
                .width(200.dp)
                .background(Color(0xFF121212))
                .padding(vertical = 16.dp)
        ) {
            Text("PipeTV", Modifier.padding(16.dp), color = Color.Red, style = MaterialTheme.typography.headlineSmall)

            // Search Button in Sidebar
            var isSearchBtnFocused by remember { mutableStateOf(false) }
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .onFocusChanged { isSearchBtnFocused = it.isFocused }
                    .focusable()
                    .clickable { isSearchActive = true },
                color = if (isSearchBtnFocused) Color.White.copy(0.2f) else Color.Transparent,
                shape = MaterialTheme.shapes.small
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Search, null, tint = Color.White)
                    Spacer(Modifier.width(8.dp))
                    Text("Search", color = Color.White)
                }
            }

            HorizontalDivider(Modifier.padding(vertical = 8.dp), color = Color.DarkGray)

            categories.forEach { category ->
                var isFocused by remember { mutableStateOf(false) }
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .onFocusChanged { isFocused = it.isFocused }
                        .focusable()
                        .clickable { 
                            isSearchActive = false
                            selectedCategory = category 
                        },
                    color = when {
                        isFocused -> Color.White.copy(0.2f)
                        !isSearchActive && selectedCategory == category -> Color.Red.copy(0.2f)
                        else -> Color.Transparent
                    },
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(category, Modifier.padding(12.dp), color = if (isFocused) Color.White else Color.Gray)
                }
            }
        }

        // --- CONTENT AREA ---
        Column(Modifier.fillMaxSize()) {
            if (isSearchActive) {
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .focusRequester(searchFocusRequester),
                    placeholder = { Text("Search via Invidious...", color = Color.Gray) },
                    singleLine = true,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF1E1E1E),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.Gray
                    )
                )
            }

            Box(Modifier.fillMaxSize()) {
                if (isLoading) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(16.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(videos) { video ->
                            VideoCard(video, repository)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VideoCard(video: InvidiousVideo, repository: InvidiousRepository) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var isFocused by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .border(width = if (isFocused) 2.dp else 0.dp, color = Color.White, shape = MaterialTheme.shapes.medium)
            .clickable {
                scope.launch {
                    repository.getStreamUrl(video.videoId)?.let { url ->
                        context.startActivity(Intent(context, VideoPlayerActivity::class.java).apply {
                            putExtra("video_url", url)
                        })
                    }
                }
            },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
    ) {
        Column {
            AsyncImage(
                model = video.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier.aspectRatio(16/9f).fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Text(video.title, Modifier.padding(8.dp), color = Color.White, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}
