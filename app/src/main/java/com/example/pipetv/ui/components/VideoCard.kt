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
                // Fix: Uses correct CardShape type
                shape = CardDefaults.shape(MaterialTheme.shapes.medium)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        // Fix: Safe call (?.) on nullable thumbnails list
                        .data(video.videoThumbnails?.firstOrNull()?.url)
                        .crossfade(true)
                        // RAM Optimization: Downscale to card size for Chromecast HD
                        .size(426, 240)
                        .build(),
                    // Fix: Provide fallback string for nullable title
                    contentDescription = video.title ?: "Video Thumbnail",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        },
        title = {
            Text(
                // Fix: Handle nullable String
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
