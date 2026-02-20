package com.example.pipetv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
                MainAppShell()
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun MainAppShell() {
    val navController = rememberNavController()
    // Tracking which screen we are on
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    // NavigationDrawer provides the side panel behavior
    NavigationDrawer(
        drawerContent = { drawerValue ->
            Column(
                Modifier.fillMaxHeight().padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Home Item
                NavigationDrawerItem(
                    selected = currentRoute == "home",
                    onClick = { navController.navigate("home") },
                    leadingContent = { Icon(Icons.Default.Home, contentDescription = null) }
                ) {
                    Text("Home")
                }

                // Search Item
                NavigationDrawerItem(
                    selected = currentRoute == "search",
                    onClick = { navController.navigate("search") },
                    leadingContent = { Icon(Icons.Default.Search, contentDescription = null) }
                ) {
                    Text("Search")
                }

                // Settings Item
                NavigationDrawerItem(
                    selected = currentRoute == "settings",
                    onClick = { navController.navigate("settings") },
                    leadingContent = { Icon(Icons.Default.Settings, contentDescription = null) }
                ) {
                    Text("Settings")
                }
            }
        }
    ) {
        // This box holds your actual screen content to the right of the panel
        Box(Modifier.fillMaxSize().padding(start = 16.dp)) {
            NavHost(navController = navController, startDestination = "home") {
                composable("home") { HomeScreen() }
                composable("search") { SearchScreen() }
                composable("settings") { SettingsScreen() }
            }
        }
    }
}
