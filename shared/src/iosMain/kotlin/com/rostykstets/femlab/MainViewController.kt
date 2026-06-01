package com.rostykstets.femlab

import androidx.compose.ui.window.ComposeUIViewController
import com.rostykstets.femlab.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }