package com.example.pipetv.ui.screens

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.pipetv.network.InvidiousRepository
import com.example.pipetv.network.InvidiousVideo
import com.example.pipetv.ui.player.VideoPlayerActivity
import kotlinx.coroutines.launch

@Composable
fun SearchScreen() {
    val context = LocalContext.current
    val repository = remember { InvidiousRepository() }
    val scope = rememberCoroutineScope()
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf(emptyList<InvidiousVideo>()) }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Search YouTube", color = Color.Gray) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = Color.Red
            )
        )

        Button(
            onClick = { scope.launch { results = repository.searchVideos(query) } },
            modifier = Modifier.padding(top = 8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Search")
        }

        Spacer(Modifier.height(16.dp))

        LazyVerticalGrid(columns = GridCells.Fixed(3)) {
            items(results) { video ->
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            scope.launch {
                                val url = repository.getStreamUrl(video.videoId)
                                val intent = Intent(context, VideoPlayerActivity::class.java).apply {
                                    putExtra("video_url", url)
                                }
                                context.startActivity(intent)
                            }
                        },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF222222))
                ) {
                    Text(
                        text = video.title,
                        modifier = Modifier.padding(12.dp),
                        color = Color.White,
                        maxLines = 2
                    )
                }
            }
        }
    }
}
