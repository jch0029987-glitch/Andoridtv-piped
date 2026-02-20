package com.example.pipetv.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import com.example.pipetv.utils.checkForUpdates

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val context = LocalContext.current

    // Using LazyColumn so settings are scrollable if you add more later
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }

        // Instance Configuration Info
        item {
            Surface(
                onClick = { /* Could open an editor dialog here */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Build, contentDescription = null)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("Invidious Instance", style = MaterialTheme.typography.labelLarge)
                        Text("http://10.78.240.3:3000", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        // Update Button (Fixes your build error)
        item {
            Surface(
                onClick = { checkForUpdates(context) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("Check for Updates", style = MaterialTheme.typography.labelLarge)
                        Text("Current Version: 1.0.0-beta", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        // About / Credits
        item {
            Surface(
                onClick = { /* Show credits */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text("About PipeTV", style = MaterialTheme.typography.labelLarge)
                        Text("Built for privacy on Android TV", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}
