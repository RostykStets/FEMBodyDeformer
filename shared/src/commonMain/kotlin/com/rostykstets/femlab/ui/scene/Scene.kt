package com.rostykstets.femlab.ui.scene

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rostykstets.femlab.domain.models.ElementFace
import com.rostykstets.femlab.domain.models.FEMBody
import com.rostykstets.femlab.domain.models.ProjectedFace
import com.rostykstets.femlab.ui.utilities.CustomText
import com.rostykstets.femlab.ui.utilities.PureBlue
import com.rostykstets.femlab.ui.utilities.VeryLightGray2
import com.rostykstets.femlab.ui.models.SimpleCamera
import com.rostykstets.femlab.ui.models.Vector3
import com.rostykstets.femlab.ui.scene.components.menus.infoMenu.InfoMenu
import com.rostykstets.femlab.ui.scene.components.menus.sideMenu.SideMenu
import com.rostykstets.femlab.ui.viewmodels.FEMViewModel
import femlab.shared.generated.resources.Res
import femlab.shared.generated.resources.baloo_bhai_2_regular
import femlab.shared.generated.resources.warning_message
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.exp

@Composable
fun Scene() {

    val viewModel: FEMViewModel = koinViewModel()

    val bodySize by viewModel.bodySize.collectAsState()
    val bodySplits by viewModel.bodySplits.collectAsState()

    val youngModule by viewModel.youngModule.collectAsState()
    val poissonRatio by viewModel.poissonRatio.collectAsState()
    val pressure by viewModel.pressure.collectAsState()

    val deformedBody by viewModel.deformedBody.collectAsState()
    val isCalculating by viewModel.isCalculating.collectAsState()

    val showWarningMessage by viewModel.showWarningMessage.collectAsState()

    SceneContent(
        bodySize = bodySize,
        bodySplits = bodySplits,
        youngModule = youngModule,
        poissonRatio = poissonRatio,
        pressure = pressure,
        deformedBody = deformedBody,
        isCalculating = isCalculating,
        femBody = viewModel.femBody,
        showWarningMessage = showWarningMessage,
        updateSize = viewModel::updateSize,
        updateSplits = viewModel::updateSplits,
        updateYoungModule = viewModel::updateYoungModule,
        updatePoissonRatio = viewModel::updatePoissonRatio,
        updatePressure = viewModel::updatePressure,
        calculate = viewModel::calculate,
        toggleFaceFixed = viewModel::toggleFaceFixed,
        toggleFaceLoaded = viewModel::toggleFaceLoaded,
        hideWarningMessage = viewModel::hideWarningMessage
    )
}

@Composable
private fun SceneContent(
    bodySize: Vector3, bodySplits: Vector3, youngModule: String,
    poissonRatio: String, pressure: String,
    deformedBody: DoubleArray?, isCalculating: Boolean,
    femBody: FEMBody,
    showWarningMessage: Boolean,
    updateSize: (Vector3) -> Unit,
    updateSplits: (Vector3) -> Unit,
    updateYoungModule: (String) -> Unit,
    updatePoissonRatio: (String) -> Unit,
    updatePressure: (String) -> Unit,
    calculate: () -> Unit,
    toggleFaceFixed: (ElementFace) -> Unit,
    toggleFaceLoaded: (ElementFace) -> Unit,
    hideWarningMessage: () -> Unit
) {

    var sizeX by remember(bodySize.x) { mutableStateOf(bodySize.x.toString()) }
    var sizeY by remember(bodySize.y) { mutableStateOf(bodySize.y.toString()) }
    var sizeZ by remember(bodySize.z) { mutableStateOf(bodySize.z.toString()) }

    var splitX by remember(bodySplits.x) { mutableStateOf(bodySplits.x.toInt().toString()) }
    var splitY by remember(bodySplits.y) { mutableStateOf(bodySplits.y.toInt().toString()) }
    var splitZ by remember(bodySplits.z) { mutableStateOf(bodySplits.z.toInt().toString()) }

    val camera = remember { SimpleCamera() }

    var showIndexes by remember { mutableStateOf(false) }

    val projectedFacesList = remember { mutableStateListOf<ProjectedFace>() }

    Box(
        modifier = Modifier
            .fillMaxHeight()
            .background(VeryLightGray2)
    ) {

        RenderBody(
            modifier = Modifier
                .faceSelectionGestures(
                    bodySize = bodySize,
                    bodySplits = bodySplits,
                    isCalculating = isCalculating,
                    projectedFacesList = projectedFacesList,
                    onToggleFixed = { toggleFaceFixed(it) },
                    onToggleLoaded = { toggleFaceLoaded(it) }
                )
                .cameraTouchControls(
                    isCalculating = isCalculating,
                    camera = camera
                )
                .cameraMouseScroll(
                    isCalculating = isCalculating,
                    camera = camera
                ),
            femBody = femBody,
            deformedBody = deformedBody,
            projectedFacesList = projectedFacesList,
            camera = camera,
            showIndexes = showIndexes
        )

        SideMenu(
            modifier = Modifier
                .align(alignment = Alignment.TopStart)
                .clickable(enabled = false, onClick = {}),
            sizeX = sizeX,
            sizeY = sizeY,
            sizeZ = sizeZ,
            splitX = splitX,
            splitY = splitY,
            splitZ = splitZ,
            bodySize = bodySize,
            bodySplits = bodySplits,
            youngModule = youngModule,
            poissonRatio = poissonRatio,
            pressure = pressure,
            isCalculating = isCalculating,
            updateSize = updateSize,
            updateSplits = updateSplits,
            updateYoungModule = updateYoungModule,
            updatePoissonRatio = updatePoissonRatio,
            updatePressure = updatePressure,
            calculate = calculate
        )

        if (isCalculating) CalculatingIndicator(modifier = Modifier.matchParentSize())

        InfoMenu(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .clickable(enabled = false, onClick = {}),
            showIndexes = showIndexes,
            toggleIndexesCheckbox = { showIndexes = it })

        if (showWarningMessage) {
            WarningMessage(hideWarningMessage)
        }
    }
}

fun Modifier.faceSelectionGestures(
    bodySize: Vector3,
    bodySplits: Vector3,
    isCalculating: Boolean,
    projectedFacesList: List<ProjectedFace>,
    onToggleFixed: (ElementFace) -> Unit,
    onToggleLoaded: (ElementFace) -> Unit
): Modifier = this.pointerInput(bodySize, bodySplits, isCalculating) {
    if (isCalculating) return@pointerInput

    detectTapGestures(
        onTap = { clickOffset ->
            val clickedFace = findClickedFace(clickOffset, projectedFacesList)
            if (clickedFace != null) onToggleFixed(clickedFace)
        },
        onLongPress = { clickOffset ->
            val clickedFace = findClickedFace(clickOffset, projectedFacesList)
            if (clickedFace != null) onToggleLoaded(clickedFace)
        }
    )
}

fun Modifier.cameraTouchControls(
    isCalculating: Boolean,
    camera: SimpleCamera
): Modifier = this.pointerInput(isCalculating) {
    if (isCalculating) return@pointerInput

    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent()
            val fingersCount = event.changes.size
            if (event.type == PointerEventType.Move) {
                val change = event.changes.firstOrNull()
                if (change != null) {
                    val dragAmount = change.position - change.previousPosition
                    if (event.buttons.isSecondaryPressed || fingersCount >= 3) {
                        camera.targetOffsetX += dragAmount.x
                        camera.targetOffsetY += dragAmount.y
                    } else if (fingersCount == 2) {
                        val change0 = event.changes.getOrNull(0)
                        val change1 = event.changes.getOrNull(1)
                        if (change0 != null && change1 != null) {
                            val currDistance = (change0.position - change1.position).getDistance()
                            val prevDistance =
                                (change0.previousPosition - change1.previousPosition).getDistance()
                            if (prevDistance > 0f) {
                                camera.scaleModifier =
                                    (camera.scaleModifier * (currDistance / prevDistance)).coerceIn(
                                        0.3f,
                                        5.0f
                                    )
                            }
                        }
                    } else if (fingersCount == 1 && !event.buttons.isSecondaryPressed) {
                        camera.angleX += dragAmount.x * 0.4f
                        camera.angleY = (camera.angleY - dragAmount.y * 0.4f).coerceIn(-85f, 85f)
                    }
                    change.consume()
                }
            }
        }
    }
}

fun Modifier.cameraMouseScroll(
    isCalculating: Boolean,
    camera: SimpleCamera
): Modifier = this.pointerInput(isCalculating) {
    if (isCalculating) return@pointerInput

    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent()
            if (event.type == PointerEventType.Scroll) {
                val change = event.changes.firstOrNull()
                if (change != null) {
                    val rawY = change.scrollDelta.y
                    if (rawY != 0f) {
                        camera.scaleModifier =
                            (camera.scaleModifier * exp(-rawY * 0.05f)).coerceIn(
                                0.3f,
                                5.0f
                            )
                        change.consume()
                    }
                }
            }
        }
    }
}

@Composable
private fun CalculatingIndicator(modifier: Modifier) {
    Box(
        modifier = modifier
            .background(color = Color.Gray.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color.Yellow,
            modifier = Modifier.size(30.dp)
        )
    }
}

@Composable
private fun RenderBody(
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

@Composable
private fun WarningMessage(hideWarningMessage: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize().background(color = Color.Gray.copy(alpha = 0.5f))
            .clickable(enabled = true, onClick = hideWarningMessage),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(140.dp).background(
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp)
                ).padding(horizontal = 5.dp),
            contentAlignment = Alignment.Center
        ) {
            CustomText(
                text = stringResource(Res.string.warning_message),
                fontSize = 13.sp,
                maxLines = 4
            )
        }
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

private fun findClickedFace(click: Offset, projectedFaces: List<ProjectedFace>): ElementFace? {

    val sortedFaces = projectedFaces.sortedByDescending { it.averageDepth }

    for (projected in sortedFaces) {
        if (projected.vertices.size < 4) continue
        if (isPointInPolygon(click, projected.vertices)) {
            return projected.face
        }
    }
    return null
}

private fun isPointInPolygon(p: Offset, poly: List<Offset>): Boolean {
    var inside = false
    var j = poly.size - 1
    for (i in poly.indices) {
        if ((poly[i].y > p.y) != (poly[j].y > p.y) &&
            (p.x < (poly[j].x - poly[i].x) * (p.y - poly[i].y) / (poly[j].y - poly[i].y) + poly[i].x)
        ) {
            inside = !inside
        }
        j = i
    }
    return inside
}

@Preview(
    widthDp = 900,
    heightDp = 600
)
@Composable
private fun ScenePreview() {
    SceneContent(
        bodySize = Vector3(4f, 4f, 4f),
        bodySplits = Vector3(2f, 1f, 2f),
        youngModule = "4",
        poissonRatio = "0.3",
        pressure = "2",
        deformedBody = null,
        isCalculating = false,
        femBody = FEMBody(),
        showWarningMessage = false,
        updateSize = {},
        updateSplits = {},
        updateYoungModule = {},
        updatePoissonRatio = {},
        updatePressure = {},
        calculate = {},
        toggleFaceFixed = {},
        toggleFaceLoaded = {},
        hideWarningMessage = {}
    )
}