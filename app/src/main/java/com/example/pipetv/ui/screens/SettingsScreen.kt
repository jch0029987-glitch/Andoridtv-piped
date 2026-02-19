package com.example.pipetv.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import com.example.pipetv.network.InvidiousRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val scope = rememberCoroutineScope()
    val repo = remember { InvidiousRepository() }
    var status by remember { mutableStateOf("Ready") }

    Column(Modifier.fillMaxSize().padding(48.dp)) {
        Text("Settings", style = MaterialTheme.typography.headlineLarge)
        Spacer(Modifier.height(32.dp))

        // Surface provides the "Click" and "Focus" behavior for TV
        Surface(
            selected = false,
            onClick = {
                scope.launch {
                    status = "Checking GitHub..."
                    val update = repo.checkForUpdates()
                    status = if (update != null) "New Update: ${update.first}" else "Up to Date"
                }
            },
            modifier = Modifier.width(400.dp)
        ) {
            ListItem(
                selected = false,
                onClick = { }, // Handled by Surface
                headlineContent = { Text("Check for Updates") },
                supportingContent = { Text(status) },
                trailingContent = { Icon(Icons.Default.Refresh, contentDescription = null) }
            )
        }

        Spacer(Modifier.height(16.dp))
        Text("User: jch0029987-glitch", style = MaterialTheme.typography.bodySmall)
        Text("Host: 10.78.240.3", style = MaterialTheme.typography.bodySmall)
    }
}
