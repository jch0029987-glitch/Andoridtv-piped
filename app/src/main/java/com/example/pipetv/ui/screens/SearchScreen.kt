package com.example.pipetv.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.tv.material3.*
import com.example.pipetv.network.InvidiousRepository
import com.example.pipetv.network.InvidiousVideo
import com.example.pipetv.ui.components.VideoCard
import com.example.pipetv.ui.player.VideoPlayerActivity
import kotlinx.coroutines.launch

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SearchScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember { InvidiousRepository() }
    
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf(emptyList<InvidiousVideo>()) }
    var isSearching by remember { mutableStateOf(false) }

    val onSearchAction = {
        if (query.isNotBlank()) {
            scope.launch {
                isSearching = true
                results = repo.searchVideos(query)
                isSearching = false
            }
        }
    }

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        // Search bar area
        Row(verticalAlignment = Alignment.CenterVertically) {
            androidx.compose.material3.OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Search Invidious") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { onSearchAction() })
            )
            
            Spacer(Modifier.width(16.dp))
            
            Button(onClick = onSearchAction) {
                Text("Search")
            }
        }

        Spacer(Modifier.height(24.dp))

        if (isSearching) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Searching your instance...")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(results) { video ->
                    VideoCard(
                        video = video,
                        onClick = {
                            val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                                putExtra("VIDEO_ID", video.videoId)
                            }
                            context.startActivity(intent)
                        }
                    )
                }
            }
        }
    }
}
