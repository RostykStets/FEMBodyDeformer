package com.rostykstets.fembodydeformer

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.rostykstets.fembodydeformer.di.initKoin

fun main() {

    initKoin()

    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "FEM Body Deformer",
        ) {
            App()
        }
    }
}