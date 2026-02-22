package com.example.pipetv.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pipetv.PipeTVApp
import com.example.pipetv.ui.components.VideoCard

@Composable
fun SearchScreen(navController: NavController, app: PipeTVApp) {
    var query by remember { mutableStateOf("") }
    // Using collectAsState with explicit initial value to satisfy the compiler
    val results by app.repository.searchResults.collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = { 
                query = it
                app.repository.searchVideos(it) 
            },
            label = { Text("Search") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            // items(results) now correctly identifies VideoItem from the list
            items(results) { video ->
                VideoCard(video = video) {
                    val intent = android.content.Intent(navController.context, com.example.pipetv.ui.player.VideoPlayerActivity::class.java).apply {
                        putExtra("VIDEO_ID", video.videoId)
                    }
                    navController.context.startActivity(intent)
                }
            }
        }
    }
}
