package com.unicofrance.uniexo.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.unicofrance.uniexo.UniExoApplication
import com.unicofrance.uniexo.ui.detail.DetailScreen
import com.unicofrance.uniexo.ui.detail.DetailViewModel
import com.unicofrance.uniexo.ui.googleMap.GoogleMapScreen
import com.unicofrance.uniexo.ui.googleMap.GoogleMapViewModel

class MainActivity : ComponentActivity() {
    private val app by lazy { application as UniExoApplication }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = "map") {
                composable("map") {
                    GoogleMapScreen(
                        modifier = Modifier.fillMaxSize(),
                        viewModel = GoogleMapViewModel(containerRepository = app.containerRepository),
                        onNavigationToDetail = { id ->
                            navController.navigate("detail/$id")
                        }
                    )
                }
                composable("detail/{id}") { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id")
                    DetailScreen(
                        modifier = Modifier.fillMaxSize(),

                        backToMap = {
                            navController.popBackStack()
                        },
                        viewModel = DetailViewModel(
                            containerRepository = app.containerRepository,
                            containerId = id
                        )
                    )
                }
            }

        }
    }
}