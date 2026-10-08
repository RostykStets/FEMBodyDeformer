package com.rostykstets.fembodydeformer.ui.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rostykstets.fembodydeformer.ui.navigation.localNavController
import com.rostykstets.fembodydeformer.ui.routing.RecordsScreen
import com.rostykstets.fembodydeformer.ui.routing.SceneScreen
import com.rostykstets.fembodydeformer.ui.screens.splash.viewmodels.PreferencesViewModel
import com.rostykstets.fembodydeformer.ui.screens.utilities.DarkRed
import com.rostykstets.fembodydeformer.ui.screens.utilities.SCREEN_HEIGHT_DESKTOP
import com.rostykstets.fembodydeformer.ui.screens.utilities.SCREEN_HEIGHT_MOBILE
import com.rostykstets.fembodydeformer.ui.screens.utilities.SCREEN_WIDTH_DESKTOP
import com.rostykstets.fembodydeformer.ui.screens.utilities.SCREEN_WIDTH_MOBILE
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SplashScreen() {

    val navController = localNavController.current

    val preferencesViewModel: PreferencesViewModel = koinViewModel()

    val isLoading by preferencesViewModel.isLoading.collectAsState()

    val lastOpenProjectId by preferencesViewModel.lastOpenProjectId.collectAsState()

    SplashContent(
        isLoading = isLoading,
        onLoadingDone = {
            if(lastOpenProjectId == -1) {
                navController.navigate(RecordsScreen)
            } else {
                navController.navigate(SceneScreen(id = lastOpenProjectId))
            }
        }
    )
}

@Composable
private fun SplashContent(isLoading: Boolean, onLoadingDone: () -> Unit) {

    LaunchedEffect(isLoading) {
        if(!isLoading) {
            onLoadingDone()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(color = Color.White),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(modifier = Modifier.size(40.dp), color = DarkRed)
    }
}

@Preview(
    widthDp = SCREEN_WIDTH_DESKTOP,
    heightDp = SCREEN_HEIGHT_DESKTOP
)
@Composable
private fun SplashDesktopPreview() {
    SplashContent(isLoading = true, onLoadingDone = {})
}

@Preview(
    widthDp = SCREEN_WIDTH_MOBILE,
    heightDp = SCREEN_HEIGHT_MOBILE
)
@Composable
private fun SplashMobilePreview() {
    SplashContent(isLoading = true, onLoadingDone = {})
}