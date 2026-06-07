package com.rostykstets.femlab.domain

import androidx.compose.runtime.mutableStateListOf
import com.rostykstets.femlab.ui.Vector3
import com.rostykstets.femlab.ui.product

class FEMBody {
    var nel: Int = 0
    var nqp: Int = 0
    var ng: Int = 0

    var nzu: Int = 0
    var nep: Int = 0

    lateinit var akt: DoubleArray
    lateinit var nt: IntArray

    lateinit var dj: Array<Array<Array<DoubleArray>>>

    lateinit var djDet: Array<DoubleArray>

    lateinit var dfixyz: Array<Array<Array<DoubleArray>>>

    lateinit var mge: Array<Array<DoubleArray>>

    lateinit var fe: Array<DoubleArray>

    lateinit var mg: Array<DoubleArray>

    lateinit var fVec: DoubleArray

    var zu: IntArray = IntArray(0)
    var zp: DoubleArray = DoubleArray(0)

    val fixedFaces = mutableStateListOf<ElementFace>()
    val loadedFaces = mutableStateListOf<ElementFace>()

    val externalFaces = mutableSetOf<ElementFace>()

    fun buildElements(bodySize: Vector3, splits: Vector3) {
        nel = splits.product()

        val nx = splits.x.toInt()
        val ny = splits.y.toInt()
        val nz = splits.z.toInt()

        nqp = calculateNqp(nx, ny, nz)
        akt = DoubleArray(3 * nqp)
        nt = IntArray(20 * nel)

        val hx = bodySize.x.toDouble() / nx
        val hy = bodySize.y.toDouble() / ny
        val hz = bodySize.z.toDouble() / nz

        val map = Array(2 * nx + 1) { Array(2 * ny + 1) { IntArray(2 * nz + 1) { 0 } } }
        var nodeCounter = 0

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

        var elementId = 1
        for (ez in 0..<nz) {
            for (ey in 0..<ny) {
                for (ex in 0..<nx) {
                    val cx = ex * 2
                    val cy = ey * 2
                    val cz = ez * 2
                    val localNodes = IntArray(20)

                    localNodes[0] = map[cx][cy][cz]
                    localNodes[1] = map[cx + 2][cy][cz]
                    localNodes[2] = map[cx + 2][cy + 2][cz]
                    localNodes[3] = map[cx][cy + 2][cz]
                    localNodes[4] = map[cx][cy][cz + 2]
                    localNodes[5] = map[cx + 2][cy][cz + 2]
                    localNodes[6] = map[cx + 2][cy + 2][cz + 2]
                    localNodes[7] = map[cx][cy + 2][cz + 2]
                    localNodes[8] = map[cx + 1][cy][cz]
                    localNodes[9] = map[cx + 2][cy + 1][cz]
                    localNodes[10] = map[cx + 1][cy + 2][cz]
                    localNodes[11] = map[cx][cy + 1][cz]
                    localNodes[12] = map[cx][cy][cz + 1]
                    localNodes[13] = map[cx + 2][cy][cz + 1]
                    localNodes[14] = map[cx + 2][cy + 2][cz + 1]
                    localNodes[15] = map[cx][cy + 2][cz + 1]
                    localNodes[16] = map[cx + 1][cy][cz + 2]
                    localNodes[17] = map[cx + 2][cy + 1][cz + 2]
                    localNodes[18] = map[cx + 1][cy + 2][cz + 2]
                    localNodes[19] = map[cx][cy + 1][cz + 2]

                    for (localNode in 1..20) {
                        nt[(localNode - 1) + (elementId - 1) * 20] = localNodes[localNode - 1]
                    }
                    elementId++
                }
            }
        }
        ng = calculateNg()

        fixedFaces.clear()
        loadedFaces.clear()

        findExternalFaces()

        updateBoundaryArrays(0.0)
    }

    private fun findExternalFaces() {
        externalFaces.clear()

        val faceSharingMap = mutableMapOf<List<Int>, MutableList<ElementFace>>()

        for (e in 1..nel) {
            for (fIdx in 1..6) {
                val nodes = getNodesForFace(e, fIdx)

                val sortedKey = nodes.sorted()

                if (!faceSharingMap.containsKey(sortedKey)) {
                    faceSharingMap[sortedKey] = mutableListOf()
                }
                faceSharingMap[sortedKey]?.add(ElementFace(e, fIdx))
            }
        }

        for ((_, sharingElements) in faceSharingMap) {
            if (sharingElements.size == 1) {
                externalFaces.add(sharingElements[0])
            }
        }
    }

    fun updateBoundaryArrays(currentPressure: Double) {

        val uniqueNodes = mutableSetOf<Int>()
        for (face in fixedFaces) {
            val faceNodes = getNodesForFace(face.elementId, face.faceIndex)

            uniqueNodes.addAll(faceNodes.toTypedArray())
        }
        nzu = uniqueNodes.size
        zu = uniqueNodes.sorted().toIntArray()

        nep = loadedFaces.size
        zp = DoubleArray(nep * 3)
        loadedFaces.forEachIndexed { idx, face ->
            val base = idx * 3
            zp[base + 0] = face.elementId.toDouble()
            zp[base + 1] = face.faceIndex.toDouble()
            zp[base + 2] = currentPressure
        }
    }

    private fun getLocalNodesForFace(faceIndex: Int): List<Int> {
        return when (faceIndex) {
            1 -> listOf(1, 4, 3, 2, 12, 11, 10, 9)
            2 -> listOf(5, 6, 7, 8, 17, 18, 19, 20)
            3 -> listOf(1, 2, 6, 5, 9, 14, 17, 13)
            4 -> listOf(2, 3, 7, 6, 10, 15, 18, 14)
            5 -> listOf(3, 4, 8, 7, 11, 16, 19, 15)
            6 -> listOf(4, 1, 5, 8, 12, 13, 20, 16)
            else -> emptyList()
        }
    }

    fun getNodesForFace(elementId: Int, faceIndex: Int): List<Int> {
        return getLocalNodesForFace(faceIndex).map { getGlobalNodeId(it, elementId) }
    }

    private fun createMGE(elementIndex: Int, e: Double, v: Double): Array<DoubleArray> {
        val elementMGE = Array(60) { DoubleArray(60) }

        val l = e * v / ((1.0 + v) * (1.0 - 2.0 * v))
        val mu = e / (2.0 * (1.0 + v))

        val a11 = Array(20) { DoubleArray(20) }
        val a22 = Array(20) { DoubleArray(20) }
        val a33 = Array(20) { DoubleArray(20) }
        val a12 = Array(20) { DoubleArray(20) }
        val a13 = Array(20) { DoubleArray(20) }
        val a23 = Array(20) { DoubleArray(20) }

        for (i in 0 until 20) {
            for (j in 0 until 20) {
                var sum11 = 0.0
                var sum22 = 0.0
                var sum33 = 0.0
                var sum12 = 0.0
                var sum13 = 0.0
                var sum23 = 0.0
                var gaussIndex = 0

                for (m in GaussConstants.gaussianConst) {
                    for (n in GaussConstants.gaussianConst) {
                        for (k in GaussConstants.gaussianConst) {
                            val weight = m * n * k
                            val det = djDet[elementIndex][gaussIndex]
                            val dfi = dfixyz[elementIndex][gaussIndex]

                            sum11 += weight * (l * (1.0 - v) / v * (dfi[i][0] * dfi[j][0]) + mu * (dfi[i][1] * dfi[j][1] + dfi[i][2] * dfi[j][2])) * det
                            sum22 += weight * (l * (1.0 - v) / v * (dfi[i][1] * dfi[j][1]) + mu * (dfi[i][0] * dfi[j][0] + dfi[i][2] * dfi[j][2])) * det
                            sum33 += weight * (l * (1.0 - v) / v * (dfi[i][2] * dfi[j][2]) + mu * (dfi[i][0] * dfi[j][0] + dfi[i][1] * dfi[j][1])) * det

                            sum12 += weight * (l * (dfi[i][0] * dfi[j][1]) + mu * (dfi[i][1] * dfi[j][0])) * det
                            sum13 += weight * (l * (dfi[i][0] * dfi[j][2]) + mu * (dfi[i][2] * dfi[j][0])) * det
                            sum23 += weight * (l * (dfi[i][1] * dfi[j][2]) + mu * (dfi[i][2] * dfi[j][1])) * det

                            gaussIndex++
                        }
                    }
                }
                a11[i][j] = sum11
                a22[i][j] = sum22
                a33[i][j] = sum33
                a12[i][j] = sum12
                a13[i][j] = sum13
                a23[i][j] = sum23
            }
        }

        for (i in 0 until 20) {
            for (j in 0 until 20) {
                elementMGE[i][j] = a11[i][j]
                elementMGE[i][20 + j] = a12[i][j]
                elementMGE[i][40 + j] = a13[i][j]

                elementMGE[20 + i][j] = a12[j][i]
                elementMGE[20 + i][20 + j] = a22[i][j]
                elementMGE[20 + i][40 + j] = a23[i][j]

                elementMGE[40 + i][j] = a13[j][i]
                elementMGE[40 + i][20 + j] = a23[j][i]
                elementMGE[40 + i][40 + j] = a33[i][j]
            }
        }
        return elementMGE
    }

    private fun calculateFE(
        pressure: Double,
        faceIndex: Int,
        faceNodesGlobal: List<Int>
    ): DoubleArray {
        val elementFE = DoubleArray(60)

        val faceCoords = Array(8) { DoubleArray(3) }
        for (i in 0 until 8) {
            val gId = faceNodesGlobal[i]
            faceCoords[i][0] = getX(gId)
            faceCoords[i][1] = getY(gId)
            faceCoords[i][2] = getZ(gId)
        }

        val dXYZdNT = Array(9) { Array(3) { DoubleArray(2) } }
        for (i in 0 until 9) {
            var sumXEta = 0.0
            var sumYEta = 0.0
            var sumZEta = 0.0
            var sumXTau = 0.0
            var sumYTau = 0.0
            var sumZTau = 0.0
            for (j in 0 until 8) {
                val pt = faceCoords[j]
                val dpsite = GaussConstants.dpsite[i][j]

                sumXEta += pt[0] * dpsite[0]
                sumYEta += pt[1] * dpsite[0]
                sumZEta += pt[2] * dpsite[0]
                sumXTau += pt[0] * dpsite[1]
                sumYTau += pt[1] * dpsite[1]
                sumZTau += pt[2] * dpsite[1]
            }

            dXYZdNT[i][0][0] = sumXEta
            dXYZdNT[i][0][1] = sumXTau
            dXYZdNT[i][1][0] = sumYEta
            dXYZdNT[i][1][1] = sumYTau
            dXYZdNT[i][2][0] = sumZEta
            dXYZdNT[i][2][1] = sumZTau
        }

        val fe1 = DoubleArray(8)
        val fe2 = DoubleArray(8)
        val fe3 = DoubleArray(8)

        var gaussIndex = 0

        for (m in GaussConstants.gaussianConst) {
            for (n in GaussConstants.gaussianConst) {
                val weight = m * n
                val jac = dXYZdNT[gaussIndex]

                val nx = jac[1][0] * jac[2][1] - jac[2][0] * jac[1][1]
                val ny = jac[2][0] * jac[0][1] - jac[0][0] * jac[2][1]
                val nz = jac[0][0] * jac[1][1] - jac[1][0] * jac[0][1]

                for (i in 0 until 8) {
                    val shapeVal = GaussConstants.dpsiteXYZdeNT[gaussIndex][i]
                    fe1[i] += weight * pressure * nx * shapeVal
                    fe2[i] += weight * pressure * ny * shapeVal
                    fe3[i] += weight * pressure * nz * shapeVal
                }
                gaussIndex++
            }
        }

        val localNodes = getLocalNodesForFace(faceIndex)
        for (i in 0 until 8) {
            val localId = localNodes[i] - 1
            elementFE[localId] = fe1[i]
            elementFE[20 + localId] = fe2[i]
            elementFE[40 + localId] = fe3[i]
        }

        return elementFE
    }

    private fun assembleGlobalSystem() {
        val totalDof = nqp * 3

        mg = Array(totalDof) { DoubleArray(ng) }
        fVec = DoubleArray(totalDof)

        for (eIndex in 0 until nel) {
            val elementId = eIndex + 1

            for (i in 0 until 60) {
                val localNodeI = (i % 20) + 1
                val axisI = i / 20
                val globalNodeI = getGlobalNodeId(localNodeI, elementId)
                val globalDofI = (globalNodeI - 1) * 3 + axisI

                fVec[globalDofI] += fe[eIndex][i]

                for (j in 0 until 60) {
                    val localNodeJ = (j % 20) + 1
                    val axisJ = j / 20
                    val globalNodeJ = getGlobalNodeId(localNodeJ, elementId)
                    val globalDofJ = (globalNodeJ - 1) * 3 + axisJ

                    if (globalDofJ >= globalDofI) {
                        val colOffset = globalDofJ - globalDofI

                        if (colOffset < ng) {
                            mg[globalDofI][colOffset] += mge[eIndex][i][j]
                        } else {
                            throw IllegalStateException("Ширина стрічки ng ($ng) занадто мала! Потрібно як мінімум ${colOffset + 1}")
                        }
                    }
                }
            }
        }

        for (globalNodeId in zu) {
            val baseDof = (globalNodeId - 1) * 3

            mg[baseDof + 0][0] = 1e16
            mg[baseDof + 1][0] = 1e16
            mg[baseDof + 2][0] = 1e16

            fVec[baseDof + 0] = 0.0
            fVec[baseDof + 1] = 0.0
            fVec[baseDof + 2] = 0.0
        }
    }

    private fun multiplyBandMatrix(
        matrix: Array<DoubleArray>,
        x: DoubleArray,
        result: DoubleArray
    ) {
        val n = x.size
        for (i in 0 until n) {
            result[i] = 0.0
        }

        for (i in 0 until n) {
            result[i] += matrix[i][0] * x[i]

            for (j in 1 until ng) {
                val col = i + j
                if (col < n) {
                    result[i] += matrix[i][j] * x[col]
                    result[col] += matrix[i][j] * x[i]
                }
            }
        }
    }

    private fun solveConjugateGradient(): DoubleArray {
        val n = fVec.size
        val u = DoubleArray(n) { 0.0 }
        val r = fVec.copyOf()
        val p = r.copyOf()
        val Ap = DoubleArray(n)

        var rsold = 0.0
        for (i in 0 until n) {
            rsold += r[i] * r[i]
        }

        val maxIter = 10000
        val epsilon = 1e-8

        for (iter in 0 until maxIter) {

            multiplyBandMatrix(mg, p, Ap)

            var pAp = 0.0
            for (i in 0 until n) {
                pAp += p[i] * Ap[i]
            }

            val alpha = rsold / pAp

            for (i in 0 until n) {
                u[i] += alpha * p[i]
                r[i] -= alpha * Ap[i]
            }

            var rsnew = 0.0
            for (i in 0 until n) {
                rsnew += r[i] * r[i]
            }

            if (kotlin.math.sqrt(rsnew) < epsilon) {
                break
            }

            val beta = rsnew / rsold
            for (i in 0 until n) {
                p[i] = r[i] + beta * p[i]
            }
            rsold = rsnew
        }

        return u
    }

    fun solve(e: Double, v: Double, pressure: Double): DoubleArray {

        calculateJacobiansAndDerivatives()

        mge = Array(nel) { Array(60) { DoubleArray(60) } }
        fe = Array(nel) { DoubleArray(60) }

        for (i in 1..nel) {
            mge[i - 1] = createMGE(i - 1, e, v)
        }

        for (loadedFace in loadedFaces) {
            val eIndex = loadedFace.elementId - 1
            val faceNodesGlobal = getNodesForFace(loadedFace.elementId, loadedFace.faceIndex)

            val faceForces = calculateFE(pressure, loadedFace.faceIndex, faceNodesGlobal)
            for (i in 0 until 60) {
                fe[eIndex][i] += faceForces[i]
            }
        }

        assembleGlobalSystem()

        val u = solveConjugateGradient()

        val deformedAkt = DoubleArray(akt.size)
        for (i in akt.indices) {
            deformedAkt[i] = akt[i] + u[i]
        }

        return deformedAkt
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
            if (diff > maxDiff) maxDiff = diff
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

    fun getGlobalNodeId(localNode: Int, elementId: Int): Int {
        return nt[(localNode - 1) + (elementId - 1) * 20]
    }

    fun calculateJacobiansAndDerivatives() {
        dj = Array(nel) { Array(27) { Array(3) { DoubleArray(3) } } }
        djDet = Array(nel) { DoubleArray(27) }
        dfixyz = Array(nel) { Array(27) { Array(20) { DoubleArray(3) } } }

        for (e in 1..nel) {
            val elementIndex = e - 1

            val cubeCoords = Array(20) { DoubleArray(3) }
            for (local in 1..20) {
                val globalId = getGlobalNodeId(local, e)
                cubeCoords[local - 1][0] = getX(globalId)
                cubeCoords[local - 1][1] = getY(globalId)
                cubeCoords[local - 1][2] = getZ(globalId)
            }

            createDJ(elementIndex, cubeCoords)

            createDFIXYZ(elementIndex)
        }
    }

    private fun createDJ(elementIndex: Int, cubeCoords: Array<DoubleArray>) {
        val eps = 1e-10

        for (i in 0 until 27) {
            var sumXA = 0.0
            var sumXB = 0.0
            var sumXG = 0.0
            var sumYA = 0.0
            var sumYB = 0.0
            var sumYG = 0.0
            var sumZA = 0.0
            var sumZB = 0.0
            var sumZG = 0.0

            for (j in 0 until 20) {
                val point = cubeCoords[j]

                val dfa = GaussConstants.dfiabg[i][j][0]
                val dfb = GaussConstants.dfiabg[i][j][1]
                val dfg = GaussConstants.dfiabg[i][j][2]

                sumXA += point[0] * dfa
                sumXB += point[0] * dfb
                sumXG += point[0] * dfg
                sumYA += point[1] * dfa
                sumYB += point[1] * dfb
                sumYG += point[1] * dfg
                sumZA += point[2] * dfa
                sumZB += point[2] * dfb
                sumZG += point[2] * dfg
            }

            dj[elementIndex][i][0][0] = cleanZero(sumXA, eps)
            dj[elementIndex][i][0][1] = cleanZero(sumYA, eps)
            dj[elementIndex][i][0][2] = cleanZero(sumZA, eps)
            dj[elementIndex][i][1][0] = cleanZero(sumXB, eps)
            dj[elementIndex][i][1][1] = cleanZero(sumYB, eps)
            dj[elementIndex][i][1][2] = cleanZero(sumZB, eps)
            dj[elementIndex][i][2][0] = cleanZero(sumXG, eps)
            dj[elementIndex][i][2][1] = cleanZero(sumYG, eps)
            dj[elementIndex][i][2][2] = cleanZero(sumZG, eps)

            val d = dj[elementIndex][i]
            djDet[elementIndex][i] = d[0][0] * d[1][1] * d[2][2] +
                    d[0][1] * d[1][2] * d[2][0] +
                    d[0][2] * d[1][0] * d[2][1] -
                    d[0][2] * d[1][1] * d[2][0] -
                    d[0][0] * d[1][2] * d[2][1] -
                    d[0][1] * d[1][0] * d[2][2]
        }
    }

    private fun cleanZero(value: Double, eps: Double): Double {
        return when {
            kotlin.math.abs(value) < eps -> 0.0
            kotlin.math.abs(value - 1.0) < eps -> 1.0
            kotlin.math.abs(value + 1.0) < eps -> -1.0
            else -> value
        }
    }

    private fun createDFIXYZ(elementIndex: Int) {
        for (i in 0 until 27) {
            val d = dj[elementIndex][i]
            val det = djDet[elementIndex][i]

            if (kotlin.math.abs(det) < 1e-12) {
                throw IllegalStateException("Якобіан вироджений (det = 0) у точці Гауса $i елемента $elementIndex. Перевірте правильність нумерації вузлів елемента!")
            }

            for (j in 0 until 20) {
                val b0 = GaussConstants.dfiabg[i][j][0]
                val b1 = GaussConstants.dfiabg[i][j][1]
                val b2 = GaussConstants.dfiabg[i][j][2]

                val detX =
                    b0 * d[1][1] * d[2][2] + d[0][1] * d[1][2] * b2 + d[0][2] * b1 * d[2][1] -
                            d[0][2] * d[1][1] * b2 - b0 * d[1][2] * d[2][1] - d[0][1] * b1 * d[2][2]

                val detY =
                    d[0][0] * b1 * d[2][2] + b0 * d[1][2] * d[2][0] + d[0][2] * d[1][0] * b2 -
                            d[0][2] * b1 * d[2][0] - d[0][0] * d[1][2] * b2 - b0 * d[1][0] * d[2][2]

                val detZ =
                    d[0][0] * d[1][1] * b2 + d[0][1] * b1 * d[2][0] + b0 * d[1][0] * d[2][1] -
                            b0 * d[1][1] * d[2][0] - d[0][0] * b1 * d[2][1] - d[0][1] * d[1][0] * b2

                dfixyz[elementIndex][i][j][0] = detX / det
                dfixyz[elementIndex][i][j][1] = detY / det
                dfixyz[elementIndex][i][j][2] = detZ / det
            }
        }
    }

    fun getX(nodeGlobalId: Int): Double = akt[(nodeGlobalId - 1) * 3 + 0]
    fun getY(nodeGlobalId: Int): Double = akt[(nodeGlobalId - 1) * 3 + 1]
    fun getZ(nodeGlobalId: Int): Double = akt[(nodeGlobalId - 1) * 3 + 2]
}