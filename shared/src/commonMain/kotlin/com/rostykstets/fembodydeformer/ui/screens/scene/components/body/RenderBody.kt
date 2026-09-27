package com.rostykstets.fembodydeformer.ui.screens.scene.components.body

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.rostykstets.fembodydeformer.domain.models.FEMBody
import com.rostykstets.fembodydeformer.domain.models.ProjectedFace
import com.rostykstets.fembodydeformer.ui.models.SimpleCamera
import com.rostykstets.fembodydeformer.ui.models.Vector3
import com.rostykstets.fembodydeformer.ui.screens.utilities.PureBlue
import femlab.shared.generated.resources.Res
import femlab.shared.generated.resources.baloo_bhai_2_regular
import org.jetbrains.compose.resources.Font

@Composable
fun RenderBody(
    modifier: Modifier = Modifier,
    femBody: FEMBody,
    deformedBody: DoubleArray?,
    projectedFacesList: MutableList<ProjectedFace>,
    camera: SimpleCamera,
    showIndexes: Boolean
) {

    val textMeasurer = rememberTextMeasurer()

    val customFontFamily = FontFamily(Font(Res.font.baloo_bhai_2_regular))

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@Canvas

        val baseScale = minOf(w, h) / 12f

        val p0 = camera.project(Vector3(0f, 0f, 0f), w, h, baseScale)
        val pX = camera.project(Vector3(2f, 0f, 0f), w, h, baseScale)
        val pY = camera.project(Vector3(0f, 0f, -2f), w, h, baseScale)
        val pZ = camera.project(Vector3(0f, 2f, 0f), w, h, baseScale)

        drawLine(Color.Red, p0, pX, strokeWidth = 4f)
        drawLine(Color.Green, p0, pY, strokeWidth = 4f)
        drawLine(Color.Blue, p0, pZ, strokeWidth = 4f)

        projectedFacesList.clear()

        drawFaces(
            femBody = femBody,
            camera = camera,
            width = w,
            height = h,
            baseScale = baseScale,
            projectedFacesList = projectedFacesList
        )

        val renderingOrder = projectedFacesList.sortedBy { it.averageDepth }

        drawBoundaryFaces(
            femBody = femBody,
            renderingOrder = renderingOrder,
            drawPath = { path, color -> drawPath(path = path, color = color) }
        )

        drawBody(
            femBody,
            camera,
            w,
            h,
            baseScale,
            isDeformed = false,
            deformedCoords = null
        )

        if (deformedBody != null) {
            drawBody(
                femBody,
                camera,
                w,
                h,
                baseScale,
                isDeformed = true,
                deformedCoords = deformedBody
            )
        }

        drawNodes(
            femBody = femBody,
            camera = camera,
            width = w,
            height = h,
            baseScale = baseScale,
            showIndexes = showIndexes,
            drawCircle = { centerPt ->
                drawCircle(
                    color = PureBlue,
                    radius = 4.5f,
                    center = centerPt
                )
            },
            drawText = { screenPt, index ->
                drawText(
                    textMeasurer = textMeasurer,
                    text = "$index",
                    topLeft = Offset(
                        x = screenPt.x + 8f,
                        y = screenPt.y - 12f
                    ),
                    style = TextStyle(
                        color = Color.Black,
                        fontSize = 11.sp,
                        fontFamily = customFontFamily
                    )
                )
            }
        )
    }
}

private fun drawFaces(
    femBody: FEMBody,
    camera: SimpleCamera,
    projectedFacesList: MutableList<ProjectedFace>,
    width: Float,
    height: Float,
    baseScale: Float
) {
    for (face in femBody.externalFaces) {
        val faceNodes =
            femBody.getNodesForFace(face.elementId, face.faceIndex)
        val screenPts = faceNodes.take(4).map { gId ->
            val mx = femBody.getX(gId).toFloat()
            val my = femBody.getY(gId).toFloat()
            val mz = femBody.getZ(gId).toFloat()
            camera.project(Vector3(mx, mz, -my), width, height, baseScale)
        }
        val diag1 = (screenPts[0] - screenPts[2]).getDistance()
        val diag2 = (screenPts[1] - screenPts[3]).getDistance()
        projectedFacesList.add(ProjectedFace(face, screenPts, diag1 + diag2))
    }
}

private fun drawBoundaryFaces(
    femBody: FEMBody,
    renderingOrder: List<ProjectedFace>,
    drawPath: (Path, Color) -> Unit
) {

    for (projected in renderingOrder) {
        val isFixed = femBody.fixedFaces.contains(projected.face)
        val isLoaded = femBody.loadedFaces.contains(projected.face)

        if (!isFixed && !isLoaded) continue

        val path = Path().apply {
            moveTo(projected.vertices[0].x, projected.vertices[0].y)
            lineTo(projected.vertices[1].x, projected.vertices[1].y)
            lineTo(projected.vertices[2].x, projected.vertices[2].y)
            lineTo(projected.vertices[3].x, projected.vertices[3].y)
            close()
        }

        val color = if (isFixed) Color.Gray.copy(alpha = 0.7f) else Color.Green.copy(
            alpha = 0.6f
        )

        drawPath(path, color)
    }
}

private fun drawNodes(
    femBody: FEMBody,
    camera: SimpleCamera,
    width: Float,
    height: Float,
    baseScale: Float,
    showIndexes: Boolean,
    drawCircle: (Offset) -> Unit,
    drawText: (Offset, Int) -> Unit
) {
    for (nodeId in 1..femBody.nqp) {
        val mscX = femBody.getX(nodeId).toFloat()
        val mscY = femBody.getY(nodeId).toFloat()
        val mscZ = femBody.getZ(nodeId).toFloat()

        val screenPt = camera.project(Vector3(mscX, mscZ, -mscY), width, height, baseScale)

        drawCircle(screenPt)

        if (showIndexes) drawText(screenPt, nodeId)
    }
}

private fun DrawScope.drawBody(
    body: FEMBody,
    camera: SimpleCamera,
    w: Float,
    h: Float,
    baseScale: Float,
    isDeformed: Boolean,
    deformedCoords: DoubleArray?
) {
    fun getScreenPos(globalNodeId: Int): Offset {
        val base = (globalNodeId - 1) * 3

        val mx =
            if (isDeformed && deformedCoords != null) deformedCoords[base + 0].toFloat() else body.getX(
                globalNodeId
            ).toFloat()
        val my =
            if (isDeformed && deformedCoords != null) deformedCoords[base + 1].toFloat() else body.getY(
                globalNodeId
            ).toFloat()
        val mz =
            if (isDeformed && deformedCoords != null) deformedCoords[base + 2].toFloat() else body.getZ(
                globalNodeId
            ).toFloat()

        return camera.project(Vector3(mx, mz, -my), w, h, baseScale)
    }

    fun getGlobalId(localNode: Int, elementId: Int): Int {
        return body.nt[(localNode - 1) + (elementId - 1) * 20]
    }

    val lineColor =
        if (isDeformed) Color.Red.copy(alpha = 0.85f) else Color.DarkGray.copy(alpha = 0.6f)
    val stroke = if (isDeformed) 2.2f else 1.5f

    val localEdges = listOf(
        1 to 9, 9 to 2,
        2 to 10, 10 to 3,
        3 to 11, 11 to 4,
        4 to 12, 12 to 1,

        1 to 13, 13 to 5,
        2 to 14, 14 to 6,
        3 to 15, 15 to 7,
        4 to 16, 16 to 8,

        5 to 17, 17 to 6,
        6 to 18, 18 to 7,
        7 to 19, 19 to 8,
        8 to 20, 20 to 5
    )

    for (e in 1..body.nel) {
        for ((localFrom, localTo) in localEdges) {
            val globalFrom = getGlobalId(localFrom, e)
            val globalTo = getGlobalId(localTo, e)

            drawLine(
                color = lineColor,
                start = getScreenPos(globalFrom),
                end = getScreenPos(globalTo),
                strokeWidth = stroke
            )
        }
    }
}