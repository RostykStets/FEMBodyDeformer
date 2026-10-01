package com.rostykstets.fembodydeformer.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.rostykstets.fembodydeformer.ui.routing.RecordsScreen
import com.rostykstets.fembodydeformer.ui.routing.SceneScreen
import com.rostykstets.fembodydeformer.ui.screens.records.RecordsScreen
import com.rostykstets.fembodydeformer.ui.screens.scene.SceneScreen

@Composable
fun NavigationGraph() {

    val navController = rememberNavController()

    CompositionLocalProvider(
        localNavController provides navController
    ) {
        NavHost(
            navController = navController,
            startDestination = RecordsScreen,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            composable<SceneScreen> { backStackEntry ->
                val sceneScreen = backStackEntry.toRoute<SceneScreen>()
                SceneScreen(simulationRecordId = sceneScreen.id)
            }

            composable<RecordsScreen> {
                RecordsScreen()
            }
        }
    }
}