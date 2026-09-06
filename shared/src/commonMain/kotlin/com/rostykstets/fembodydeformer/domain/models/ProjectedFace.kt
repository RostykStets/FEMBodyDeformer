package com.rostykstets.fembodydeformer.domain.models

import androidx.compose.ui.geometry.Offset

data class ProjectedFace(
    val face: ElementFace,
    val vertices: List<Offset>,
    val averageDepth: Float
)