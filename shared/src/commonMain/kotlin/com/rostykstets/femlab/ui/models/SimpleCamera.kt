package com.rostykstets.femlab.ui.models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class SimpleCamera {
    var angleX by mutableStateOf(45f)
    var angleY by mutableStateOf(30f)
    var scaleModifier by mutableStateOf(1.0f)

    var targetOffsetX by mutableStateOf(0f)
    var targetOffsetY by mutableStateOf(0f)

    fun project(point: Vector3, width: Float, height: Float, baseScale: Float): Offset {
        val radX = angleX * (PI / 180.0)
        val radY = angleY * (PI / 180.0)

        val x1 = point.x * cos(radX).toFloat() - point.z * sin(radX).toFloat()
        val z1 = point.x * sin(radX).toFloat() + point.z * cos(radX).toFloat()

        val y2 = point.y * cos(radY).toFloat() - z1 * sin(radY).toFloat()
        val z2 = point.y * sin(radY).toFloat() + z1 * cos(radY).toFloat()

        val distance = 15f
        val perspective = distance / (distance - z2)
        val finalScale = baseScale * scaleModifier

        val screenX = (width / 2f) + (x1 * finalScale * perspective) + targetOffsetX
        val screenY = (height / 2f) - (y2 * finalScale * perspective) + targetOffsetY

        return Offset(screenX, screenY)
    }
}