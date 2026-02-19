package com.example.pipetv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import androidx.tv.material3.* // Ensure this is the TV version
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.example.pipetv.ui.screens.*
import com.example.pipetv.ui.theme.PipeTVTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PipeTVTheme {
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
        // NavigationRail is part of androidx.tv.material3
        androidx.tv.material3.NavigationRail(
            content = {
                androidx.tv.material3.NavigationRailItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0; navController.navigate("home") },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") }
                )
                androidx.tv.material3.NavigationRailItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1; navController.navigate("search") },
                    icon = { Icon(Icons.Default.Search, contentDescription = "Search") }
                )
                androidx.tv.material3.NavigationRailItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2; navController.navigate("settings") },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") }
                )
            }
        )

        Box(Modifier.fillMaxSize()) {
            NavHost(navController, startDestination = "home") {
                composable("home") { HomeScreen() }
                composable("search") { SearchScreen() }
                composable("settings") { SettingsScreen() }
            }
        }
    }
}
