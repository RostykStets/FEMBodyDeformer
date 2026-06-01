package com.rostykstets.femlab.domain

import kotlin.math.sqrt

object GaussConstants {

    val gaussianCoords = doubleArrayOf(-sqrt(0.6), 0.0, sqrt(0.6))

    val gaussianConst = doubleArrayOf(5.0 / 9.0, 8.0 / 9.0, 5.0 / 9.0)

    val localPoints2D = arrayOf(
        doubleArrayOf(-1.0, -1.0), doubleArrayOf(1.0, -1.0), doubleArrayOf(1.0, 1.0), doubleArrayOf(-1.0, 1.0),
        doubleArrayOf(0.0, -1.0), doubleArrayOf(1.0, 0.0), doubleArrayOf(0.0, 1.0), doubleArrayOf(-1.0, 0.0)
    )

    val localPoints3D = arrayOf(
        doubleArrayOf(-1.0, 1.0, -1.0), doubleArrayOf(1.0, 1.0, -1.0), doubleArrayOf(1.0, -1.0, -1.0), doubleArrayOf(-1.0, -1.0, -1.0),
        doubleArrayOf(-1.0, 1.0, 1.0), doubleArrayOf(1.0, 1.0, 1.0), doubleArrayOf(1.0, -1.0, 1.0), doubleArrayOf(-1.0, -1.0, 1.0),
        doubleArrayOf(0.0, 1.0, -1.0), doubleArrayOf(1.0, 0.0, -1.0), doubleArrayOf(0.0, -1.0, -1.0), doubleArrayOf(-1.0, 0.0, -1.0),
        doubleArrayOf(-1.0, 1.0, 0.0), doubleArrayOf(1.0, 1.0, 0.0), doubleArrayOf(1.0, -1.0, 0.0), doubleArrayOf(-1.0, -1.0, 0.0),
        doubleArrayOf(0.0, 1.0, 1.0), doubleArrayOf(1.0, 0.0, 1.0), doubleArrayOf(0.0, -1.0, 1.0), doubleArrayOf(-1.0, 0.0, 1.0)
    )

    val dfiabg = Array(27) { Array(20) { DoubleArray(3) } }

    val dpsite = Array(9) { Array(8) { DoubleArray(2) } }

    val dpsiteXYZdeNT = Array(9) { DoubleArray(8) }

    init {
        calculateDFIABG()
        calculateDPSITE()
        calculateDPsiteXYZdeNT()
    }

    private fun calculateDFIABG() {
        for (k1 in gaussianCoords.indices) {
            val gamma = gaussianCoords[k1]
            for (k2 in gaussianCoords.indices) {
                val beta = gaussianCoords[k2]
                for (k3 in gaussianCoords.indices) {
                    val alpha = gaussianCoords[k3]

                    val index = k1 * 9 + k2 * 3 + k3

                    for (i in localPoints3D.indices) {
                        val point = localPoints3D[i]
                        if (i <= 7) {
                            dfiabg[index][i] = dfiabg18(alpha, beta, gamma, point[0], point[1], point[2])
                        } else {
                            dfiabg[index][i] = dfiabg14(alpha, beta, gamma, point[0], point[1], point[2])
                        }
                    }
                }
            }
        }
    }

    private fun dfiabg18(alpha: Double, beta: Double, gamma: Double, x: Double, y: Double, z: Double): DoubleArray {
        return doubleArrayOf(
            (1.0 / 8.0) * (1 + beta * y) * (1 + gamma * z) * (x * (-2 + alpha * x + gamma * z + beta * y) + x * (1 + alpha * x)),
            (1.0 / 8.0) * (1 + alpha * x) * (1 + gamma * z) * (y * (-2 + alpha * x + gamma * z + beta * y) + y * (1 + beta * y)),
            (1.0 / 8.0) * (1 + beta * y) * (1 + alpha * x) * (z * (-2 + alpha * x + gamma * z + beta * y) + z * (1 + gamma * z))
        )
    }

    private fun dfiabg14(alpha: Double, beta: Double, gamma: Double, alphaI: Double, betaI: Double, gammaI: Double): DoubleArray {
        return doubleArrayOf(
            (1.0 / 4.0) * (1 + beta * betaI) * (1 + gamma * gammaI) *
                    (alphaI * (-betaI * betaI * gammaI * gammaI * alpha * alpha - beta * beta * gammaI * gammaI * alphaI * alphaI -
                            betaI * betaI * gamma * gamma * alphaI * alphaI + 1) - (2 * betaI * betaI * gammaI * gammaI * alpha) * (alpha * alphaI + 1)),

            (1.0 / 4.0) * (1 + alpha * alphaI) * (1 + gamma * gammaI) *
                    (betaI * (-betaI * betaI * gammaI * gammaI * alpha * alpha - beta * beta * gammaI * gammaI * alphaI * alphaI -
                            betaI * betaI * gamma * gamma * alphaI * alphaI + 1) - (2 * beta * gammaI * gammaI * alphaI * alphaI) * (betaI * beta + 1)),

            (1.0 / 4.0) * (1 + beta * betaI) * (1 + alpha * alphaI) *
                    (gammaI * (-betaI * betaI * gammaI * gammaI * alpha * alpha - beta * beta * gammaI * gammaI * alphaI * alphaI -
                            betaI * betaI * gamma * gamma * alphaI * alphaI + 1) - (2 * betaI * betaI * gamma * alphaI * alphaI) * (gamma * gammaI + 1))
        )
    }

    private fun calculateDPSITE() {
        for (k1 in gaussianCoords.indices) {
            val eta = gaussianCoords[k1]
            for (k2 in gaussianCoords.indices) {
                val tau = gaussianCoords[k2]

                val index = k1 * 3 + k2

                for (i in localPoints2D.indices) {
                    val point = localPoints2D[i]
                    if (i < 4) {
                        dpsite[index][i] = psint14der(eta, tau, point[0], point[1])
                    } else if (i == 4 || i == 6) {
                        dpsite[index][i] = psint57der(eta, tau, point[0], point[1])
                    } else if (i == 5 || i == 7) {
                        dpsite[index][i] = psint68der(eta, tau, point[0], point[1])
                    }
                }
            }
        }
    }

    private fun psint14der(eta: Double, tau: Double, x: Double, y: Double): DoubleArray {
        return doubleArrayOf(
            (1.0 / 4.0) * (tau * y + 1) * (x * (x * eta + y * tau - 1) + x * (x * eta + 1)),
            (1.0 / 4.0) * (x * eta + 1) * (y * (x * eta + y * tau - 1) + y * (y * tau + 1))
        )
    }

    private fun psint57der(eta: Double, tau: Double, x: Double, y: Double): DoubleArray {
        return doubleArrayOf(
            (-tau * y - 1) * eta,
            (1.0 / 2.0) * (1 - eta * eta) * y
        )
    }

    private fun psint68der(eta: Double, tau: Double, x: Double, y: Double): DoubleArray {
        return doubleArrayOf(
            (1.0 / 2.0) * (1 - tau * tau) * x,
            (-eta * x - 1) * tau
        )
    }

    private fun calculateDPsiteXYZdeNT() {
        for (k1 in gaussianCoords.indices) {
            val eta = gaussianCoords[k1]
            for (k2 in gaussianCoords.indices) {
                val tau = gaussianCoords[k2]

                val index = k1 * 3 + k2

                for (i in localPoints2D.indices) {
                    val point = localPoints2D[i]
                    if (i < 4) {
                        dpsiteXYZdeNT[index][i] = psint14(eta, tau, point[0], point[1])
                    } else if (i == 4 || i == 6) {
                        dpsiteXYZdeNT[index][i] = psint57(eta, tau, point[0], point[1])
                    } else if (i == 5 || i == 7) {
                        dpsiteXYZdeNT[index][i] = psint68(eta, tau, point[0], point[1])
                    }
                }
            }
        }
    }

    private fun psint14(eta: Double, tau: Double, x: Double, y: Double): Double {
        return (1.0 / 4.0) * (tau * y + 1) * (eta * x + 1) * (eta * x + y * tau - 1)
    }

    private fun psint57(eta: Double, tau: Double, x: Double, y: Double): Double {
        return (1.0 / 2.0) * (-eta * eta + 1) * (y * tau + 1)
    }

    private fun psint68(eta: Double, tau: Double, x: Double, y: Double): Double {
        return (1.0 / 2.0) * (-tau * tau + 1) * (x * eta + 1)
    }
}