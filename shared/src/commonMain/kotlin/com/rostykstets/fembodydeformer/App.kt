package com.rostykstets.fembodydeformer

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.rostykstets.fembodydeformer.ui.navigation.NavigationGraph

@Composable
@Preview
fun App() {
    MaterialTheme {
        NavigationGraph()
    }
}