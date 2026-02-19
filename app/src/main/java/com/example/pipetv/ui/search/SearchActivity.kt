package com.example.pipetv.ui.search

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import com.example.pipetv.network.InvidiousRepository
import com.example.pipetv.network.InvidiousVideo
import com.example.pipetv.ui.player.VideoPlayerActivity
import kotlinx.coroutines.launch

class SearchActivity : ComponentActivity() {
    private val repository = InvidiousRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var searchQuery by remember { mutableStateOf("") }
            var results by remember { mutableStateOf(listOf<InvidiousVideo>()) }
            var isLoading by remember { mutableStateOf(false) }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF121212))
                    .padding(16.dp)
            ) {
                // TV Optimized Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Search YouTube (PdaNet Stealth)", color = Color.Gray) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color.Red,
                        unfocusedBorderColor = Color.Gray
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {
                        if (searchQuery.isNotBlank()) {
                            isLoading = true
                            lifecycleScope.launch {
                                results = repository.searchVideos(searchQuery)
                                isLoading = false
                            }
                        }
                    })
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.Red)
                    }
                } else {
                    // Results Grid
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4), // 4 columns looks best on TV
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(results) { video ->
                            VideoCard(video = video) {
                                lifecycleScope.launch {
                                    val streamUrl = repository.getStreamUrl(video.videoId)
                                    if (streamUrl != null) {
                                        val intent = Intent(this@SearchActivity, VideoPlayerActivity::class.java).apply {
                                            putExtra("video_url", streamUrl)
                                        }
                                        startActivity(intent)
                                    } else {
                                        Toast.makeText(this@SearchActivity, "Stream unavailable", Toast.LENGTH_SHORT).show()
                                    }
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
fun VideoCard(video: InvidiousVideo, onClick: () -> Unit) {
    var isFocused by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .width(200.dp)
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onClick() }
            .background(if (isFocused) Color.White.copy(alpha = 0.1f) else Color.Transparent)
            .padding(8.dp)
    ) {
        // Thumbnail
        AsyncImage(
            model = video.thumbnailUrl,
            contentDescription = video.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(112.dp)
                .background(Color.DarkGray),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Title
        Text(
            text = video.title,
            color = if (isFocused) Color.Red else Color.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

