package com.rostykstets.fembodydeformer.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rostykstets.fembodydeformer.ui.routing.RecordsScreen
import com.rostykstets.fembodydeformer.ui.routing.SceneScreen
import com.rostykstets.fembodydeformer.ui.scene.Scene

@Composable
fun NavigationGraph() {

    val navController = rememberNavController()

    CompositionLocalProvider(
        localNavController provides navController
    ) {
        NavHost(
            navController = navController,
            startDestination = SceneScreen,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            composable<SceneScreen> {
                Scene()
            }

            composable<RecordsScreen> { }
        }
    }
}