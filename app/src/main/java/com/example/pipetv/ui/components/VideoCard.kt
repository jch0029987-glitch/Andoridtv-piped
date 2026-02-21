package com.example.pipetv.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.pipetv.data.models.VideoItem

@Composable
fun VideoCard(video: VideoItem, onClick: () -> Unit) {
    val viewCountText = if (video.viewCount > 0) "${video.viewCount} views" else ""

    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column {
            AsyncImage(
                model = video.thumbnailUrl,
                contentDescription = null,
                modifier = Modifier.height(120.dp).fillMaxWidth()
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = video.title, maxLines = 2, style = MaterialTheme.typography.titleMedium)
                Text(text = video.author, style = MaterialTheme.typography.bodySmall)
                if (viewCountText.isNotEmpty()) {
                    Text(text = viewCountText, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
