package com.example.pipetv.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.*
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.pipetv.network.InvidiousVideo

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun VideoCard(
    video: InvidiousVideo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    StandardCardContainer(
        modifier = modifier.width(240.dp),
        imageCard = {
            Card(
                onClick = onClick,
                modifier = Modifier.aspectRatio(16f / 9f),
                // FIX 1: Use CardDefaults.shape to match the expected type
                shape = CardDefaults.shape(MaterialTheme.shapes.medium)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(video.videoThumbnails?.firstOrNull()?.url)
                        // FIX 2: Coil 3 handles hardware bitmaps by default.
                        // We use crossfade for GPU-accelerated transitions.
                        .crossfade(true)
                        .build(),
                    contentDescription = video.title ?: "Video Thumbnail",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        },
        title = {
            Text(
                text = video.title ?: "Unknown Title",
                style = MaterialTheme.typography.labelMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
            )
        },
        subtitle = {
            Text(
                text = "${video.author ?: "Unknown"} • ${video.viewCountText ?: ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                maxLines = 1
            )
        }
    )
}
