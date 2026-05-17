package com.rostykstets.femlab.ui

import androidx.compose.ui.geometry.Offset
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

class SimpleCamera {
    var angleX = 45f
    var angleY = 30f
    var scaleModifier = 1.0f

    fun project(point: Vector3, width: Float, height: Float, baseScale: Float): Offset {

        val radX = angleX * (PI / 180.0)
        val radY = angleY * (PI / 180.0)

        val x1 = point.x * cos(radX) - point.z * sin(radX)
        val z1 = point.x * sin(radX) + point.z * cos(radX)

        val y2 = point.y * cos(radY) - z1 * sin(radY)
        val z2 = point.y * sin(radY) + z1 * cos(radY)

        val distance = 15f
        val perspective = distance / (distance - z2.toFloat())
        val finalScale = baseScale * scaleModifier

        val screenX = (width / 2f) + (x1.toFloat() * finalScale * perspective)
        val screenY = (height / 2f) - (y2.toFloat() * finalScale * perspective)

        return Offset(screenX, screenY)
    }
}