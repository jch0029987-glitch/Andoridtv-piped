package com.example.pipetv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pipetv.ui.screens.SearchScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            var selectedRoute by remember { mutableStateOf("home") }

            Row(Modifier.fillMaxSize().background(Color.Black)) {
                // PERSISTENT SIDE PANEL
                NavigationRail(
                    containerColor = Color(0xFF1A1A1A),
                    modifier = Modifier.width(100.dp)
                ) {
                    Spacer(Modifier.height(16.dp))
                    
                    NavigationRailItem(
                        selected = selectedRoute == "home",
                        onClick = { 
                            selectedRoute = "home"
                            navController.navigate("home") 
                        },
                        icon = { Icon(Icons.Default.Home, "Home") },
                        label = { Text("Home") },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = Color.Red,
                            indicatorColor = Color.White.copy(0.1f)
                        )
                    )

                    NavigationRailItem(
                        selected = selectedRoute == "search",
                        onClick = { 
                            selectedRoute = "search"
                            navController.navigate("search") 
                        },
                        icon = { Icon(Icons.Default.Search, "Search") },
                        label = { Text("Search") },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = Color.Red,
                            indicatorColor = Color.White.copy(0.1f)
                        )
                    )
                }

                // DYNAMIC CONTENT
                Box(Modifier.fillMaxSize()) {
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") { 
                            Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                                Text("Home Feed Coming Soon", color = Color.White)
                            }
                        }
                        composable("search") { SearchScreen() }
                    }
                }
            }
        }
    }
}
