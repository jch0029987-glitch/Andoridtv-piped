package com.example.pipetv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.*
import androidx.tv.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import com.example.pipetv.ui.screens.*
import com.example.pipetv.ui.theme.PipeTVTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PipeTVTheme {
                // This ensures the whole app shell is in a Composable context
                MainAppShell()
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MainAppShell() {
    val navController = rememberNavController()
    var selectedTab by remember { mutableIntStateOf(0) }

    Row(Modifier.fillMaxSize()) {
        // We call the TV-specific components explicitly by their full path 
        // to avoid any confusion with the standard mobile versions.
        androidx.tv.material3.NavigationRail(
            modifier = Modifier.fillMaxHeight(),
        ) {
            // HOME TAB
            androidx.tv.material3.NavigationRailItem(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0; navController.navigate("home") },
                icon = { androidx.tv.material3.Icon(Icons.Default.Home, contentDescription = "Home") }
            )

            // SEARCH TAB
            androidx.tv.material3.NavigationRailItem(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1; navController.navigate("search") },
                icon = { androidx.tv.material3.Icon(Icons.Default.Search, contentDescription = "Search") }
            )

            // SETTINGS TAB
            androidx.tv.material3.NavigationRailItem(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2; navController.navigate("settings") },
                icon = { androidx.tv.material3.Icon(Icons.Default.Settings, contentDescription = "Settings") }
            )
        }

        // Screen Content
        Box(Modifier.fillMaxSize()) {
            NavHost(navController = navController, startDestination = "home") {
                composable("home") { HomeScreen() }
                composable("search") { SearchScreen() }
                composable("settings") { SettingsScreen() }
            }
        }
    }
}
