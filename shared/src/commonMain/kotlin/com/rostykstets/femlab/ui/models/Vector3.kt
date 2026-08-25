package com.rostykstets.femlab.ui.models

data class Vector3(var x: Float, var y: Float, var z: Float)

fun Vector3.product(): Int {
    return (x * y * z).toInt()
}