package com.rostykstets.femlab.domain

import androidx.compose.runtime.mutableStateListOf
import com.rostykstets.femlab.ui.Vector3
import com.rostykstets.femlab.ui.product

class FEMBody {
    var nel: Int = 0
    var nqp: Int = 0
    var ng: Int = 0

    // Динамічні лічильники крайових умов
    var nzu: Int = 0
    var nep: Int = 0

    lateinit var akt: DoubleArray
    lateinit var nt: IntArray

    lateinit var dj: Array<Array<Array<DoubleArray>>>

    // djDet: [nel][27 точок Гауса] - Визначник матриці Якобі
    lateinit var djDet: Array<DoubleArray>

    // dfixyz: [nel][27 точок Гауса][20 вузлів][3 осі] - Глобальні похідні функцій форми
    lateinit var dfixyz: Array<Array<Array<DoubleArray>>>

    // Головні структури крайових умов
    var zu: IntArray = IntArray(0)
    var zp: DoubleArray = DoubleArray(0) // Лінеаризований масив [elementId, faceIdx, pressure, ...]

    // Інтерактивні набори вибраних граней (щоб уникнути дублікатів)
    val fixedFaces = mutableStateListOf<ElementFace>() // Грані, закріплені користувачем (Сірі)
    val loadedFaces = mutableStateListOf<ElementFace>() // Грані під тиском (Зелені)

    val externalFaces = mutableSetOf<ElementFace>()

    fun buildElements(bodySize: Vector3, splits: Vector3) {
        nel = splits.product()

        val nx = splits.x.toInt()
        val ny = splits.y.toInt()
        val nz = splits.z.toInt()

        nqp = calculateNqp(nx, ny, nz)
        akt = DoubleArray(3 * nqp)
        nt = IntArray(20 * nel)

        // Рахуємо крок сітки у Double, щоб не втратити точність!
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

                        // Множимо на половину кроку (hx / 2.0)
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

        // Очищуємо старі вибори при зміні розмірів сітки
        fixedFaces.clear()
        loadedFaces.clear()

        findExternalFaces()

        updateBoundaryArrays(0.0)
    }

    // Метод, який фільтрує грані, залишаючи лише унікальні зовнішні поверхні
    private fun findExternalFaces() {
        externalFaces.clear()

        // Тимчасова мапа, де ключ — це відсортований набір з 8 глобальних вузлів грані,
        // а значення — список елементів, яким ця грань належить
        val faceSharingMap = mutableMapOf<List<Int>, MutableList<ElementFace>>()

        for (e in 1..nel) {
            for (fIdx in 1..6) {
                // Отримуємо 8 глобальних вузлів для поточної грані
                val nodes = getNodesForFace(e, fIdx)
                // Сортуємо список вузлів, щоб однакові грані у різних елементів мали ідентичний ключ
                val sortedKey = nodes.sorted()

                if (!faceSharingMap.containsKey(sortedKey)) {
                    faceSharingMap[sortedKey] = mutableListOf()
                }
                faceSharingMap[sortedKey]?.add(ElementFace(e, fIdx))
            }
        }

        // Критерій граничної (зовнішньої) грані: вона належить лише ОДНОМУ скінченному елементу
        for ((_, sharingElements) in faceSharingMap) {
            if (sharingElements.size == 1) {
                externalFaces.add(sharingElements[0])
            }
        }
    }

    // МАТЕМАТИЧНЕ ОБЧИСЛЕННЯ ЗНАЧЕНЬ nzu, nep ТА ФОРМУВАННЯ КІНЦЕВИХ МАСИВІВ ZU / ZP
    fun updateBoundaryArrays(currentPressure: Double) {
        // 1. Обчислення для Закріплень (ZU)
        val uniqueNodes = mutableSetOf<Int>()
        for (face in fixedFaces) {
            val faceNodes = getNodesForFace(face.elementId, face.faceIndex)
            // Додаємо всі 8 вузлів цієї грані в Set (автоматично прибирає дублікати на стиках)
            uniqueNodes.addAll(faceNodes.toTypedArray())
        }
        nzu = uniqueNodes.size
        zu = uniqueNodes.sorted().toIntArray() // Масив ZU готовий

        // 2. Обчислення для Навантажень (ZP)
        nep = loadedFaces.size
        zp = DoubleArray(nep * 3)
        loadedFaces.forEachIndexed { idx, face ->
            val base = idx * 3
            zp[base + 0] = face.elementId.toDouble()
            zp[base + 1] = face.faceIndex.toDouble()
            zp[base + 2] = currentPressure
        }
    }

    // Повертає список із 8 локальних вузлів, що утворюють конкретну грань елемента (згідно з Рис. 2)
    fun getNodesForFace(elementId: Int, faceIndex: Int): List<Int> {
        val localIndices = when (faceIndex) {
            1 -> listOf(1, 4, 3, 2, 12, 11, 10, 9)       // Нижня грань (z = min)
            2 -> listOf(5, 6, 7, 8, 17, 18, 19, 20)      // Верхня грань (z = max)
            3 -> listOf(1, 2, 6, 5, 9, 14, 17, 13)       // Передня грань (y = min)
            4 -> listOf(2, 3, 7, 6, 10, 15, 18, 14)      // Права бічна (x = max)
            5 -> listOf(3, 4, 8, 7, 11, 16, 19, 15)      // Задня грань (y = max)
            6 -> listOf(4, 1, 5, 8, 12, 13, 20, 16)      // Ліва бічна (x = min)
            else -> emptyList()
        }
        return localIndices.map { getGlobalNodeId(it, elementId) }
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

            // Дістаємо координати 20 вузлів поточного елемента
            val cubeCoords = Array(20) { DoubleArray(3) }
            for (local in 1..20) {
                val globalId = getGlobalNodeId(local, e)
                cubeCoords[local - 1][0] = getX(globalId)
                cubeCoords[local - 1][1] = getY(globalId)
                cubeCoords[local - 1][2] = getZ(globalId)
            }

            // 3.a) Рахуємо Якобіан для 27 точок Гауса цього елемента
            createDJ(elementIndex, cubeCoords)

            // 3.b) Рахуємо Глобальні похідні для цього елемента
            createDFIXYZ(elementIndex)
        }
    }

    private fun createDJ(elementIndex: Int, cubeCoords: Array<DoubleArray>) {
        val eps = 1e-10

        for (i in 0 until 27) {
            var sumXA = 0.0; var sumXB = 0.0; var sumXG = 0.0
            var sumYA = 0.0; var sumYB = 0.0; var sumYG = 0.0
            var sumZA = 0.0; var sumZB = 0.0; var sumZG = 0.0

            for (j in 0 until 20) {
                val point = cubeCoords[j]

                // Звертаємося до нашого закешованого об'єкта з Кроку 2
                val dfa = GaussConstants.dfiabg[i][j][0]
                val dfb = GaussConstants.dfiabg[i][j][1]
                val dfg = GaussConstants.dfiabg[i][j][2]

                sumXA += point[0] * dfa; sumXB += point[0] * dfb; sumXG += point[0] * dfg
                sumYA += point[1] * dfa; sumYB += point[1] * dfb; sumYG += point[1] * dfg
                sumZA += point[2] * dfa; sumZB += point[2] * dfb; sumZG += point[2] * dfg
            }

            // Формуємо матрицю 3x3 з усуненням мікропохибок (округленням)
            dj[elementIndex][i][0][0] = cleanZero(sumXA, eps); dj[elementIndex][i][0][1] = cleanZero(sumYA, eps); dj[elementIndex][i][0][2] = cleanZero(sumZA, eps)
            dj[elementIndex][i][1][0] = cleanZero(sumXB, eps); dj[elementIndex][i][1][1] = cleanZero(sumYB, eps); dj[elementIndex][i][1][2] = cleanZero(sumZB, eps)
            dj[elementIndex][i][2][0] = cleanZero(sumXG, eps); dj[elementIndex][i][2][1] = cleanZero(sumYG, eps); dj[elementIndex][i][2][2] = cleanZero(sumZG, eps)

            // Обчислення визначника Якобіана (за Правилом Саррюса)
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

            // Захист від вироджених (сплеснутих або вивернутих) елементів
            if (kotlin.math.abs(det) < 1e-12) {
                throw IllegalStateException("Якобіан вироджений (det = 0) у точці Гауса $i елемента $elementIndex. Перевірте правильність нумерації вузлів елемента!")
            }

            for (j in 0 until 20) {
                // Вектор локальних похідних для конкретного вузла
                val b0 = GaussConstants.dfiabg[i][j][0]
                val b1 = GaussConstants.dfiabg[i][j][1]
                val b2 = GaussConstants.dfiabg[i][j][2]

                // Швидке аналітичне розв'язання системи 3x3 за методом Крамера
                // Обчислюємо визначники 3-х підматриць, замінюючи відповідні стовпці на вектор вільних членів (b)
                val detX = b0 * d[1][1] * d[2][2] + d[0][1] * d[1][2] * b2 + d[0][2] * b1 * d[2][1] -
                        d[0][2] * d[1][1] * b2 - b0 * d[1][2] * d[2][1] - d[0][1] * b1 * d[2][2]

                val detY = d[0][0] * b1 * d[2][2] + b0 * d[1][2] * d[2][0] + d[0][2] * d[1][0] * b2 -
                        d[0][2] * b1 * d[2][0] - d[0][0] * d[1][2] * b2 - b0 * d[1][0] * d[2][2]

                val detZ = d[0][0] * d[1][1] * b2 + d[0][1] * b1 * d[2][0] + b0 * d[1][0] * d[2][1] -
                        b0 * d[1][1] * d[2][0] - d[0][0] * b1 * d[2][1] - d[0][1] * d[1][0] * b2

                // Фінальні глобальні похідні (ділимо на головний визначник)
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