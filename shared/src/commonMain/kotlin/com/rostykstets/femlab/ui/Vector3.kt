package com.rostykstets.femlab.ui

data class Vector3(var x: Float, var y: Float, var z: Float) {
    fun subtract(other: Vector3) = Vector3(this.x - other.x, this.y - other.y, this.z - other.z)
}

fun buildBodyShape(size: Vector3, splits: Vector3): List<Vector3> {
    val body = mutableListOf<Vector3>()
    val sX = splits.x * 2
    val sY = splits.y * 2
    val sZ = splits.z * 2
    val offset = Vector3(size.x / 2f, 0f, size.z / 2f)

    for (y in 0..sY.toInt()) {
        for (x in sX.toInt() downTo 0) {
            for (z in sZ.toInt() downTo 0) {
                if ((x % 2 != 0 && z % 2 != 0) || (x % 2 != 0 && y % 2 != 0) || (y % 2 != 0 && z % 2 != 0)) {
                    continue
                }
                val point = Vector3((x * size.x) / sX, (y * size.y) / sY, (z * size.z) / sZ)
                body.add(point.subtract(offset))
            }
        }
    }
    return body
}

fun Vector3.product(): Int {
    return (x * y * z).toInt()
}