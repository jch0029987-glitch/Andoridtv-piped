package com.example.pipetv.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue // FIXED: Required for 'by' delegate
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.pipetv.PipeTVApp
import com.example.pipetv.ui.components.VideoCard

@Composable
fun SearchScreen(navController: NavController, app: PipeTVApp) {
    var query by remember { mutableStateOf("") }
    // FIXED: Correct repository flow reference
    val results by app.repository.searchResults.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = { 
                query = it
                app.repository.searchVideos(it) 
            },
            label = { Text("Search") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            // FIXED: Passing the actual list of results
            items(results) { video ->
                VideoCard(video = video) { /* Play Logic */ }
            }
        }
    }
}
