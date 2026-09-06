package com.rostykstets.fembodydeformer

import androidx.compose.ui.window.ComposeUIViewController
import com.rostykstets.fembodydeformer.di.initKoin

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) { App() }