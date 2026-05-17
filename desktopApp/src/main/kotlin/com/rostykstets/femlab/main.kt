package com.rostykstets.femlab

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "FEM Lab",
    ) {
        App()
    }
}