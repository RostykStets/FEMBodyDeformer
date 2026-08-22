package com.rostykstets.femlab.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rostykstets.femlab.domain.ElementFace
import com.rostykstets.femlab.domain.FEMBody
import com.rostykstets.femlab.domain.ProjectedFace
import com.rostykstets.femlab.ui.components.CustomText
import com.rostykstets.femlab.ui.components.PureBlue
import com.rostykstets.femlab.ui.components.VeryLightGray
import com.rostykstets.femlab.ui.components.VeryLightGray2
import femlab.shared.generated.resources.Res
import femlab.shared.generated.resources.axes
import femlab.shared.generated.resources.axis_x
import femlab.shared.generated.resources.axis_y
import femlab.shared.generated.resources.axis_z
import femlab.shared.generated.resources.calculate
import femlab.shared.generated.resources.calculating
import femlab.shared.generated.resources.dimensions
import femlab.shared.generated.resources.parameters
import femlab.shared.generated.resources.poisson_ratio
import femlab.shared.generated.resources.pressure
import femlab.shared.generated.resources.size_x
import femlab.shared.generated.resources.size_y
import femlab.shared.generated.resources.size_z
import femlab.shared.generated.resources.split_x
import femlab.shared.generated.resources.split_y
import femlab.shared.generated.resources.split_z
import femlab.shared.generated.resources.splits
import femlab.shared.generated.resources.young_module
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

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

    SceneContent(
        bodySize = bodySize,
        bodySplits = bodySplits,
        youngModule = youngModule,
        poissonRatio = poissonRatio,
        pressure = pressure,
        deformedBody = deformedBody,
        isCalculating = isCalculating,
        femBody = viewModel.femBody,
        updateSize = viewModel::updateSize,
        updateSplits = viewModel::updateSplits,
        updateYoungModule = viewModel::updateYoungModule,
        updatePoissonRatio = viewModel::updatePoissonRatio,
        updatePressure = viewModel::updatePressure,
        calculate = viewModel::calculate,
        toggleFaceFixed = viewModel::toggleFaceFixed,
        toggleFaceLoaded = viewModel::toggleFaceLoaded
    )
}

@Composable
private fun SceneContent(
    bodySize: Vector3, bodySplits: Vector3, youngModule: String,
    poissonRatio: String, pressure: String,
    deformedBody: DoubleArray?, isCalculating: Boolean,
    femBody: FEMBody,
    updateSize: (Vector3) -> Unit,
    updateSplits: (Vector3) -> Unit,
    updateYoungModule: (String) -> Unit,
    updatePoissonRatio: (String) -> Unit,
    updatePressure: (String) -> Unit,
    calculate: () -> Unit,
    toggleFaceFixed: (ElementFace) -> Unit,
    toggleFaceLoaded: (ElementFace) -> Unit,
) {

    var sizeXText by remember(bodySize.x) { mutableStateOf(bodySize.x.toString()) }
    var sizeYText by remember(bodySize.y) { mutableStateOf(bodySize.y.toString()) }
    var sizeZText by remember(bodySize.z) { mutableStateOf(bodySize.z.toString()) }

    var splitXText by remember(bodySplits.x) { mutableStateOf(bodySplits.x.toInt().toString()) }
    var splitYText by remember(bodySplits.y) { mutableStateOf(bodySplits.y.toInt().toString()) }
    var splitZText by remember(bodySplits.z) { mutableStateOf(bodySplits.z.toInt().toString()) }

    val camera = remember { SimpleCamera() }

    val labelFontSize = 18.sp

    Row(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .width(180.dp)
                .background(color = Color.White)
                .verticalScroll(rememberScrollState())
                .padding(6.dp)
                .fillMaxHeight()
        ) {
            CustomText(
                modifier = Modifier.padding(bottom = 2.dp),
                text = stringResource(Res.string.dimensions),
                fontSize = labelFontSize
            )

            OutlinedTextField(
                value = sizeXText,
                onValueChange = {
                    sizeXText = it; it.toFloatOrNull()
                    ?.let { v -> updateSize(bodySize.copy(x = v)) }
                },
                label = { CustomText(text = stringResource(Res.string.size_x)) },
                singleLine = true,
                enabled = !isCalculating
            )

            OutlinedTextField(
                value = sizeYText,
                onValueChange = {
                    sizeYText = it; it.toFloatOrNull()
                    ?.let { v -> updateSize(bodySize.copy(y = v)) }
                },
                label = { CustomText(text = stringResource(Res.string.size_y)) },
                singleLine = true,
                enabled = !isCalculating
            )

            OutlinedTextField(
                value = sizeZText,
                onValueChange = {
                    sizeZText = it; it.toFloatOrNull()
                    ?.let { v -> updateSize(bodySize.copy(z = v)) }
                },
                label = { CustomText(text = stringResource(Res.string.size_z)) },
                singleLine = true,
                enabled = !isCalculating
            )

            Spacer(modifier = Modifier.height(8.dp))

            CustomText(
                modifier = Modifier.padding(bottom = 2.dp),
                text = stringResource(Res.string.splits),
                fontSize = labelFontSize
            )

            OutlinedTextField(
                value = splitXText,
                onValueChange = {
                    splitXText = it; it.toFloatOrNull()
                    ?.let { v -> updateSplits(bodySplits.copy(x = v)) }
                },
                label = { CustomText(text = stringResource(Res.string.split_x)) },
                singleLine = true,
                enabled = !isCalculating
            )

            OutlinedTextField(
                value = splitYText,
                onValueChange = {
                    splitYText = it; it.toFloatOrNull()
                    ?.let { v -> updateSplits(bodySplits.copy(y = v)) }
                },
                label = { CustomText(text = stringResource(Res.string.split_y)) },
                singleLine = true,
                enabled = !isCalculating
            )

            OutlinedTextField(
                value = splitZText,
                onValueChange = {
                    splitZText = it
                    it.toFloatOrNull()?.let { v -> updateSplits(bodySplits.copy(z = v)) }
                },
                label = { CustomText(text = stringResource(Res.string.split_z)) },
                singleLine = true,
                enabled = !isCalculating
            )

            Spacer(modifier = Modifier.height(8.dp))

            CustomText(
                modifier = Modifier.padding(bottom = 2.dp),
                text = stringResource(Res.string.parameters),
                fontSize = labelFontSize
            )

            OutlinedTextField(
                value = youngModule,
                onValueChange = { updateYoungModule(it) },
                label = { CustomText(text = stringResource(Res.string.young_module)) },
                singleLine = true,
                enabled = !isCalculating
            )

            OutlinedTextField(
                value = poissonRatio,
                onValueChange = { updatePoissonRatio(it) },
                label = {
                    CustomText(
                        text = stringResource(Res.string.poisson_ratio),
                        fontSize = 13.sp
                    )
                },
                singleLine = true,
                enabled = !isCalculating
            )

            OutlinedTextField(
                value = pressure,
                onValueChange = { updatePressure(it) },
                label = { CustomText(text = stringResource(Res.string.pressure)) },
                singleLine = true,
                enabled = !isCalculating
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = calculate,
                enabled = !isCalculating,
                modifier = Modifier.fillMaxWidth().height(42.dp),
                shape = RoundedCornerShape(6.dp)
            ) {
                CustomText(
                    text = if (isCalculating) stringResource(Res.string.calculating) else
                        stringResource(Res.string.calculate),
                    color = if (isCalculating) Color.Black else Color.White
                )
            }
        }

        val projectedFacesList = remember { mutableStateListOf<ProjectedFace>() }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .background(VeryLightGray2)
                .pointerInput(bodySize, bodySplits) {
                    detectTapGestures(
                        onTap = { clickOffset ->
                            if (!isCalculating) {
                                val clickedFace = findClickedFace(clickOffset, projectedFacesList)
                                if (clickedFace != null) toggleFaceFixed(clickedFace)
                            }
                        },
                        onLongPress = { clickOffset ->
                            if (!isCalculating) {
                                val clickedFace = findClickedFace(clickOffset, projectedFacesList)
                                if (clickedFace != null) toggleFaceLoaded(clickedFace)
                            }
                        }
                    )
                }
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            val fingersCount = event.changes.size
                            if (event.type == PointerEventType.Move) {
                                val change = event.changes.firstOrNull()
                                if (change != null && !isCalculating) {
                                    val dragAmount = change.position - change.previousPosition
                                    if (event.buttons.isSecondaryPressed || fingersCount >= 3) {
                                        camera.targetOffsetX += dragAmount.x
                                        camera.targetOffsetY += dragAmount.y
                                    } else if (fingersCount == 2) {
                                        val change0 = event.changes.getOrNull(0)
                                        val change1 = event.changes.getOrNull(1)
                                        if (change0 != null && change1 != null) {
                                            val currDistance =
                                                (change0.position - change1.position).getDistance()
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
                                        camera.angleY =
                                            (camera.angleY - dragAmount.y * 0.4f).coerceIn(
                                                -85f,
                                                85f
                                            )
                                    }
                                    change.consume()
                                }
                            }
                        }
                    }
                }
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            if (event.type == PointerEventType.Scroll && !isCalculating) {
                                val change = event.changes.firstOrNull()
                                if (change != null) {
                                    val rawY = change.scrollDelta.y
                                    if (rawY != 0f) {
                                        camera.scaleModifier =
                                            (camera.scaleModifier * kotlin.math.exp(-rawY * 0.05f)).coerceIn(
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
        ) {

            if (isCalculating)
                CircularProgressIndicator(
                    color = Color.Yellow,
                    modifier = Modifier.size(30.dp).align(alignment = Alignment.Center)
                )

            Canvas(modifier = Modifier.fillMaxSize()) {
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
                for (face in femBody.externalFaces) {
                    val faceNodes =
                        femBody.getNodesForFace(face.elementId, face.faceIndex)
                    val screenPts = faceNodes.take(4).map { gId ->
                        val mx = femBody.getX(gId).toFloat()
                        val my = femBody.getY(gId).toFloat()
                        val mz = femBody.getZ(gId).toFloat()
                        camera.project(Vector3(mx, mz, -my), w, h, baseScale)
                    }
                    val diag1 = (screenPts[0] - screenPts[2]).getDistance()
                    val diag2 = (screenPts[1] - screenPts[3]).getDistance()
                    projectedFacesList.add(ProjectedFace(face, screenPts, diag1 + diag2))
                }

                val renderingOrder = projectedFacesList.sortedBy { it.averageDepth }
                for (projected in renderingOrder) {
                    val isFixed = femBody.fixedFaces.contains(projected.face)
                    val isLoaded = femBody.loadedFaces.contains(projected.face)

                    if (isFixed || isLoaded) {
                        val path = Path().apply {
                            moveTo(projected.vertices[0].x, projected.vertices[0].y)
                            lineTo(projected.vertices[1].x, projected.vertices[1].y)
                            lineTo(projected.vertices[2].x, projected.vertices[2].y)
                            lineTo(projected.vertices[3].x, projected.vertices[3].y)
                            close()
                        }
                        drawPath(
                            path = path,
                            color = if (isFixed) Color.Gray.copy(alpha = 0.7f) else Color.Green.copy(
                                alpha = 0.6f
                            )
                        )
                    }
                }

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

                for (nodeId in 1..femBody.nqp) {
                    val mscX = femBody.getX(nodeId).toFloat()
                    val mscY = femBody.getY(nodeId).toFloat()
                    val mscZ = femBody.getZ(nodeId).toFloat()

                    val screenPt = camera.project(Vector3(mscX, mscZ, -mscY), w, h, baseScale)

                    drawCircle(color = PureBlue, radius = 4.5f, center = screenPt)
                }
            }

            InfoPanel(modifier = Modifier.align(Alignment.TopEnd))
        }
    }
}

@Composable
private fun InfoPanel(modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .padding(top = 12.dp, end = 12.dp)
                .width(150.dp)
                .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                .border(1.dp, VeryLightGray, RoundedCornerShape(8.dp))
                .padding(8.dp)
        ) {
            CustomText(
                modifier = Modifier.padding(bottom = 4.dp),
                text = stringResource(Res.string.axes),
                fontSize = 13.sp,
                color = Color.Gray
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(12.dp, 3.dp).background(Color.Red))

                Spacer(modifier = Modifier.width(6.dp))

                CustomText(text = stringResource(Res.string.axis_x))
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(12.dp, 3.dp).background(Color.Green))

                Spacer(modifier = Modifier.width(6.dp))

                CustomText(text = stringResource(Res.string.axis_y))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(12.dp, 3.dp).background(Color.Blue))

                Spacer(modifier = Modifier.width(6.dp))

                CustomText(text = stringResource(Res.string.axis_z))
            }
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
    widthDp = 1200,
    heightDp = 540
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
        updateSize = {},
        updateSplits = {},
        updateYoungModule = {},
        updatePoissonRatio = {},
        updatePressure = {},
        calculate = {},
        toggleFaceFixed = {},
        toggleFaceLoaded = {}
    )
}