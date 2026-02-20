package com.example.pipetv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import androidx.tv.material3.*
import androidx.compose.material3.Text
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
    var selected by remember { mutableStateOf("home") }

    NavigationDrawer(
        drawerContent = {
            Column {
                NavigationDrawerItem(
                    selected = selected == "home",
                    onClick = {
                        selected = "home"
                        navController.navigate("home")
                    }
                ) { Text("Home") }

                NavigationDrawerItem(
                    selected = selected == "search",
                    onClick = {
                        selected = "search"
                        navController.navigate("search")
                    }
                ) { Text("Search") }

                NavigationDrawerItem(
                    selected = selected == "settings",
                    onClick = {
                        selected = "settings"
                        navController.navigate("settings")
                    }
                ) { Text("Settings") }
            }
        }
    ) {
        NavHost(navController = navController, startDestination = "home") {
            composable("home") { HomeScreen() }
            composable("search") { SearchScreen() }
            composable("settings") { SettingsScreen() }
        }
    }
}
