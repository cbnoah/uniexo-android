package com.unicofrance.uniexo.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
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

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController, startDestination = "map",
                enterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start, tween(500)
                    )
                },
                exitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Start,
                        tween(500)
                    )
                },
                popEnterTransition = {
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.End, tween(500)
                    )
                },
                popExitTransition = {
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.End,
                        tween(500)
                    )
                }
            ) {
                composable("map") {
                    val mapViewModel: GoogleMapViewModel = viewModel {
                        GoogleMapViewModel(containerRepository = app.containerRepository)
                    }
                    GoogleMapScreen(
                        modifier = Modifier.fillMaxSize(),
                        viewModel = mapViewModel,
                        onNavigationToDetail = { id ->
                            navController.navigate("detail/$id")
                        }
                    )
                }
                composable("detail/{id}") { backStackEntry ->
                    val id = backStackEntry.arguments?.getString("id")
                    val detailViewModel: DetailViewModel = viewModel {
                        DetailViewModel(
                            containerRepository = app.containerRepository,
                            containerId = id
                        )
                    }

                    DetailScreen(
                        modifier = Modifier.fillMaxSize(),

                        backToMap = {
                            navController.popBackStack()
                        },
                        viewModel = detailViewModel
                    )
                }
            }
        }
    }
}