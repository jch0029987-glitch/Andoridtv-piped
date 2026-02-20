package com.example.pipetv.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
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
        modifier = modifier
            .width(240.dp)
            .graphicsLayer {
                // GPU Optimization: Treats the card as a single texture
                compositingStrategy = CompositingStrategy.ModulateAlpha
            },
        imageCard = {
            Card(
                onClick = onClick,
                modifier = Modifier.aspectRatio(16f / 9f),
                shape = CardDefaults.shape(MaterialTheme.shapes.medium)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        // FIX: Added ?. for safe call and used the first thumbnail's URL
                        .data(video.videoThumbnails?.firstOrNull()?.url)
                        .crossfade(true)
                        // Chromecast HD Optimization: Prevents OOM by scaling to card size
                        .size(426, 240)
                        .build(),
                    // FIX: Fallback string for null titles to satisfy contentDescription (String)
                    contentDescription = video.title ?: "Video Thumbnail",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        },
        title = {
            Text(
                // FIX: Fallback for title
                text = video.title ?: "Unknown Title",
                style = MaterialTheme.typography.labelMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
            )
        },
        subtitle = {
            Text(
                // FIX: Fallback for author/views
                text = "${video.author ?: "Unknown"} • ${video.viewCountText ?: ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                maxLines = 1
            )
        }
    )
}
