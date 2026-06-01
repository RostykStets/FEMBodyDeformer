package com.rostykstets.femlab

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.rostykstets.femlab.di.initKoin

fun main() {

    initKoin()

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "FEM Lab",
        ) {
            App()
        }
    }
}