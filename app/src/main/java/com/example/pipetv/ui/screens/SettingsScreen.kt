package com.example.pipetv.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.*
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

        // Update Card
        Surface(
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
                headlineContent = { Text("Check for Updates") },
                supportingContent = { Text(status) },
                trailingContent = { Icon(androidx.compose.material.icons.Icons.Default.Refresh, null) }
            )
        }

        Spacer(Modifier.height(16.dp))
        Text("Self-Hosted Instance: 10.78.240.3", style = MaterialTheme.typography.bodySmall)
    }
}
