package com.example.pipetv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import androidx.tv.material3.*   // TV Material (package name is material3)
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
    var selectedTab by remember { mutableIntStateOf(0) }

    Row(Modifier.fillMaxSize()) {

        // TV Navigation Rail
        NavigationRail(
            modifier = Modifier.fillMaxHeight(),
            header = { Spacer(Modifier.height(16.dp)) }
        ) {
            NavigationRailItem(
                selected = selectedTab == 0,
                onClick = {
                    selectedTab = 0
                    navController.navigate("home")
                },
                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                label = { Text("Home") }
            )

            NavigationRailItem(
                selected = selectedTab == 1,
                onClick = {
                    selectedTab = 1
                    navController.navigate("search")
                },
                icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                label = { Text("Search") }
            )

            NavigationRailItem(
                selected = selectedTab == 2,
                onClick = {
                    selectedTab = 2
                    navController.navigate("settings")
                },
                icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                label = { Text("Settings") }
            )
        }

        // Main content area
        Box(Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = "home"
            ) {
                composable("home") { HomeScreen() }
                composable("search") { SearchScreen() }
                composable("settings") { SettingsScreen() }
            }
        }
    }
}
