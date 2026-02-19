package com.example.pipetv.ui.search

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.pipetv.network.InvidiousRepository
import com.example.pipetv.ui.player.VideoPlayerActivity
import kotlinx.coroutines.launch

class SearchActivity : ComponentActivity() {
    private val repository = InvidiousRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var query by remember { mutableStateOf("") }
            var results by remember { mutableStateOf(emptyList<com.example.pipetv.network.InvidiousVideo>()) }

            Column(Modifier.fillMaxSize().background(Color.Black).padding(16.dp)) {
                TextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Search") },
                    trailingIcon = {
                        Button(onClick = {
                            lifecycleScope.launch {
                                results = repository.searchVideos(query)
                            }
                        }) { Text("Go") }
                    }
                )

                LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.fillMaxSize()) {
                    items(results) { video ->
                        Card(
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    lifecycleScope.launch {
                                        val url = repository.getStreamUrl(video.videoId)
                                        if (url != null) {
                                            val intent = Intent(this@SearchActivity, VideoPlayerActivity::class.java)
                                            intent.putExtra("video_url", url)
                                            startActivity(intent)
                                        }
                                    }
                                }
                        ) {
                            Text(video.title, modifier = Modifier.padding(8.dp), maxLines = 2)
                        }
                    }
                }
            }
        }
    }
}
