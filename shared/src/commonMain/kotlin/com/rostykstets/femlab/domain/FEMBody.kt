package com.rostykstets.femlab.domain

import com.rostykstets.femlab.ui.Vector3
import com.rostykstets.femlab.ui.product

class FEMBody {

    var nel: Int = 0
    var nqp: Int = 0
    var ng: Int = 0

    lateinit var akt: DoubleArray
    lateinit var nt: IntArray

    fun buildElements(bodySize: Vector3, splits: Vector3) {
        nel = splits.product()

        val ax = bodySize.x.toInt()
        val ay = bodySize.y.toInt()
        val az = bodySize.z.toInt()

        val nx = splits.x.toInt()
        val ny = splits.y.toInt()
        val nz = splits.z.toInt()

        nqp = calculateNqp(nx, ny, nz)

        akt = DoubleArray(3 * nqp)
        nt = IntArray(20 * nel)

        val hx = ax / nx
        val hy = ay / ny
        val hz = az / nz

        val map = Array(2 * nx + 1) { Array(2 * ny + 1) { IntArray(2 * nz + 1) { 0 } } }
        var nodeCounter = 0

        // КРОК 1: Заповнення АКТ та побудова карти індексів map (без змін)
        for (i in 0..(2 * nz)) {
            for (y in 0..(2 * ny)) {
                for (k in 0..(2 * nx)) {
                    val xEven = (k % 2 == 0)
                    val yEven = (y % 2 == 0)
                    val zEven = (i % 2 == 0)

                    val isValidNode = (xEven && yEven) || (xEven && zEven) || (yEven && zEven)

                    if (isValidNode) {
                        nodeCounter++
                        map[k][y][i] = nodeCounter

                        val base = (nodeCounter - 1) * 3
                        akt[base + 0] = k * (hx / 2.0)
                        akt[base + 1] = y * (hy / 2.0)
                        akt[base + 2] = i * (hz / 2.0)
                    }
                }
            }
        }

        // КРОК 2: Генерація NT — Варіант "ЗА МЕТОДИЧКОЮ" (Строга відповідність Рис. 2)
        var elementId = 1
        for (ez in 0..<nz) {
            for (ey in 0..<ny) {
                for (ex in 0..<nx) {

                    val cx = ex * 2
                    val cy = ey * 2
                    val cz = ez * 2

                    // Масив локальних відповідностей для поточного елемента (розмір 20)
                    val localNodes = IntArray(20)

                    // 1. Кутові вузли основи (1..4)
                    localNodes[0] = map[cx][cy][cz]          // Вузол 1
                    localNodes[1] = map[cx + 2][cy][cz]      // Вузол 2
                    localNodes[2] = map[cx + 2][cy + 2][cz]  // Вузол 3
                    localNodes[3] = map[cx][cy + 2][cz]      // Вузол 4

                    // 2. Кутові вузли верхньої кришки (5..8)
                    localNodes[4] = map[cx][cy][cz + 2]      // Вузол 5
                    localNodes[5] = map[cx + 2][cy][cz + 2]  // Вузол 6
                    localNodes[6] = map[cx + 2][cy + 2][cz + 2] // Вузол 7
                    localNodes[7] = map[cx][cy + 2][cz + 2]  // Вузол 8

                    // 3. Середини ребер нижньої основи (9..12)
                    localNodes[8] = map[cx + 1][cy][cz]      // Вузол 9
                    localNodes[9] = map[cx + 2][cy + 1][cz]  // Вузол 10
                    localNodes[10] = map[cx + 1][cy + 2][cz] // Вузол 11
                    localNodes[11] = map[cx][cy + 1][cz]     // Вузол 12

                    // 4. Середини вертикальних ребер (13..16)
                    localNodes[12] = map[cx][cy][cz + 1]     // Вузол 13
                    localNodes[13] = map[cx + 2][cy][cz + 1] // Вузол 14
                    localNodes[14] = map[cx + 2][cy + 2][cz + 1] // Вузол 15
                    localNodes[15] = map[cx][cy + 2][cz + 1] // Вузол 16

                    // 5. Середини ребер верхньої кришки (17..20)
                    localNodes[16] = map[cx + 1][cy][cz + 2] // Вузол 17
                    localNodes[17] = map[cx + 2][cy + 1][cz + 2] // Вузол 18
                    localNodes[18] = map[cx + 1][cy + 2][cz + 2] // Вузол 19
                    localNodes[19] = map[cx][cy + 1][cz + 2] // Вузол 20

                    // Переносимо сформовані 20 вузлів у глобальний одновимірний масив NT
                    for (localNode in 1..20) {
                        val ntIndex = (localNode - 1) + (elementId - 1) * 20
                        nt[ntIndex] = localNodes[localNode - 1]
                    }

                    elementId++
                }
            }
        }

        // КРОК 3: Обчислення півширини стрічки ng (без змін)
        ng = calculateNg()
    }

    private fun calculateNg(): Int {
        var maxDiff = 0
        for (e in 1..nel) {
            var minNode = Int.MAX_VALUE
            var maxNode = Int.MIN_VALUE
            for (local in 1..20) {
                val globalId = nt[(local - 1) + (e - 1) * 20]
                if (globalId < minNode) minNode = globalId
                if (globalId > maxNode) maxNode = globalId
            }
            val diff = maxNode - minNode + 1
            if (diff > maxDiff) {
                maxDiff = diff
            }
        }
        return 3 * maxDiff
    }

    private fun calculateNqp(nx: Int, ny: Int, nz: Int): Int {
        var nodeCounter = 0
        for (i in 0..<(2 * nz + 1)) {
            for (y in 0..<(2 * ny + 1)) {
                for (k in 0..<(2 * nx + 1)) {
                    val xEven = (k % 2 == 0)
                    val yEven = (y % 2 == 0)
                    val zEven = (i % 2 == 0)
                    val isValidNode = (xEven && yEven) || (xEven && zEven) || (yEven && zEven)
                    if (isValidNode) nodeCounter++
                }
            }
        }
        return nodeCounter
    }

    // Замість математичного запису NT[localNode, elementId]
    fun getGlobalNodeId(localNode: Int, elementId: Int): Int {
        // Формула лінеаризації двовимірної матриці (стовпчиковий варіант):
        return nt[(localNode - 1) + (elementId - 1) * 20]
    }

    fun getX(nodeGlobalId: Int): Double = akt[(nodeGlobalId - 1) * 3 + 0]
    fun getY(nodeGlobalId: Int): Double = akt[(nodeGlobalId - 1) * 3 + 1]
    fun getZ(nodeGlobalId: Int): Double = akt[(nodeGlobalId - 1) * 3 + 2]
}