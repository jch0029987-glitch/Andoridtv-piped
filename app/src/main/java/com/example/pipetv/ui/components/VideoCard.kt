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
                shape = ClickableSurfaceDefaults.shape(MaterialTheme.shapes.medium)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        // Fix 1: Added ?. for safe call on thumbnails list
                        .data(video.videoThumbnails?.firstOrNull()?.url) 
                        .allowHardware(true) // GPU optimization
                        .crossfade(true)
                        .build(),
                    // Fix 2: Added ?: "" to provide a fallback string if title is null
                    contentDescription = video.title ?: "Video Thumbnail",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        },
        title = {
            Text(
                // Fix 2: Provide fallback for title
                text = video.title ?: "Unknown Title",
                style = MaterialTheme.typography.labelMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 8.dp)
            )
        },
        subtitle = {
            Text(
                // Fix 2: Provide fallback for author and view count
                text = "${video.author ?: "Unknown"} • ${video.viewCountText ?: ""}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                maxLines = 1
            )
        }
    )
}
