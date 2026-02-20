package com.example.pipetv.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.tv.material3.*
import androidx.tv.foundation.lazy.grid.*
import com.example.pipetv.network.InvidiousRepository
import com.example.pipetv.network.InvidiousVideo
import kotlinx.coroutines.launch

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SearchScreen() {
    var query by remember { mutableStateOf("") }
    var results by remember { mutableStateOf(emptyList<InvidiousVideo>()) }
    var isLoading by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    val repo = remember { InvidiousRepository() }

    // Helper function to trigger the actual search
    val performSearch = {
        if (query.isNotBlank()) {
            scope.launch {
                isLoading = true
                results = repo.searchVideos(query)
                isLoading = false
            }
        }
    }

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // TV-friendly OutlinedTextField
            androidx.compose.material3.OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Search videos...") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                // These two lines fix the "Enter" key on the remote
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { performSearch() })
            )

            Spacer(Modifier.width(16.dp))

            // Physical Search Button (Backup for D-pad users)
            Button(onClick = { performSearch() }) {
                Text("Search")
            }
        }

        Spacer(Modifier.height(24.dp))

        if (isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Searching your instance...")
            }
        } else {
            // Using TvLazyVerticalGrid so the remote can navigate the results
            TvLazyVerticalGrid(
                columns = TvGridCells.Fixed(4),
                contentPadding = PaddingValues(bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(results) { video ->
                    // Assuming you have a VideoCard component defined elsewhere
                    VideoCard(video = video)
                }
            }
        }
    }
}
