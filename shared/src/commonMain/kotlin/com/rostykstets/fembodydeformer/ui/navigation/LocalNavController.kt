package com.rostykstets.fembodydeformer.ui.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavController

val localNavController = staticCompositionLocalOf<NavController> {
    error("Can't access NavController")
}