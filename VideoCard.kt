package com.example.pipetv.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.*
import coil.compose.AsyncImage
import com.example.pipetv.network.InvidiousVideo

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun VideoCard(
    video: InvidiousVideo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // StandardCardContainer handles the focus state and scaling for TV
    StandardCardContainer(
        imageCard = {
            Card(
                onClick = onClick,
                modifier = Modifier.aspectRatio(16f / 9f)
            ) {
                AsyncImage(
                    model = video.videoThumbnails.firstOrNull()?.url ?: "",
                    contentDescription = video.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        },
        title = {
            Text(
                text = video.title,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
            )
        },
        subtitle = {
            Text(
                text = "${video.author} • ${video.viewCountText}",
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1
            )
        },
        modifier = modifier.width(200.dp)
    )
}
