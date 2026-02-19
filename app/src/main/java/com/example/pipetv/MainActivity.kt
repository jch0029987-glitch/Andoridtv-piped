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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pipetv.ui.screens.HomeScreen
import com.example.pipetv.ui.screens.SearchScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainApp()
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    // Track the current route to highlight the side panel correctly
    var selectedRoute by remember { mutableStateOf("home") }

    Row(Modifier.fillMaxSize().background(Color.Black)) {
        // SIDE PANEL (Navigation Rail)
        NavigationRail(
            containerColor = Color(0xFF1A1A1A),
            modifier = Modifier.width(100.dp),
            header = {
                Text("PipeTV", color = Color.Red, fontSize = 18.sp, modifier = Modifier.padding(vertical = 16.dp))
            }
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column {
                    NavigationRailItem(
                        selected = selectedRoute == "home",
                        onClick = { 
                            selectedRoute = "home"
                            navController.navigate("home") {
                                popUpTo("home") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = Color.Red,
                            unselectedIconColor = Color.Gray,
                            indicatorColor = Color.White.copy(0.1f)
                        )
                    )

                    Spacer(Modifier.height(8.dp))

                    NavigationRailItem(
                        selected = selectedRoute == "search",
                        onClick = { 
                            selectedRoute = "search"
                            navController.navigate("search") {
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        label = { Text("Search") },
                        colors = NavigationRailItemDefaults.colors(
                            selectedIconColor = Color.Red,
                            unselectedIconColor = Color.Gray,
                            indicatorColor = Color.White.copy(0.1f)
                        )
                    )
                }

                // Small version tag at the bottom for your GitHub builds
                Text(
                    text = "v1.0.2", 
                    color = Color.DarkGray, 
                    fontSize = 10.sp, 
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }

        // CONTENT AREA
        Box(Modifier.fillMaxSize()) {
            NavHost(
                navController = navController, 
                startDestination = "home",
                modifier = Modifier.fillMaxSize()
            ) {
                composable("home") { 
                    HomeScreen() 
                }
                composable("search") { 
                    SearchScreen() 
                }
            }
        }
    }
}
