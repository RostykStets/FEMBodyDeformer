package com.rostykstets.femlab.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rostykstets.femlab.domain.models.ElementFace
import com.rostykstets.femlab.domain.models.FEMBody
import com.rostykstets.femlab.domain.models.ProjectedFace
import com.rostykstets.femlab.ui.utilities.CustomText
import com.rostykstets.femlab.ui.utilities.PureBlue
import com.rostykstets.femlab.ui.utilities.VeryLightGray
import com.rostykstets.femlab.ui.utilities.VeryLightGray2
import com.rostykstets.femlab.ui.models.SimpleCamera
import com.rostykstets.femlab.ui.models.Vector3
import com.rostykstets.femlab.ui.viewmodels.FEMViewModel
import femlab.shared.generated.resources.Res
import femlab.shared.generated.resources.axes
import femlab.shared.generated.resources.axis_x
import femlab.shared.generated.resources.axis_y
import femlab.shared.generated.resources.axis_z
import femlab.shared.generated.resources.baloo_bhai_2_regular
import femlab.shared.generated.resources.calculate
import femlab.shared.generated.resources.calculating
import femlab.shared.generated.resources.dimensions
import femlab.shared.generated.resources.fixed_face_info
import femlab.shared.generated.resources.fixed_face_type
import femlab.shared.generated.resources.left_angle_bracket
import femlab.shared.generated.resources.loaded_face_info
import femlab.shared.generated.resources.loaded_face_type
import femlab.shared.generated.resources.right_angle_bracket
import femlab.shared.generated.resources.parameters
import femlab.shared.generated.resources.poisson_ratio
import femlab.shared.generated.resources.pressure
import femlab.shared.generated.resources.show_indexes
import femlab.shared.generated.resources.size_x
import femlab.shared.generated.resources.size_y
import femlab.shared.generated.resources.size_z
import femlab.shared.generated.resources.split_x
import femlab.shared.generated.resources.split_y
import femlab.shared.generated.resources.split_z
import femlab.shared.generated.resources.splits
import femlab.shared.generated.resources.young_module
import org.jetbrains.compose.resources.Font
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
            )
    ) {

        SideMenu(
            modifier = Modifier.align(alignment = Alignment.TopStart),
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

        RenderBody(
            femBody = femBody,
            deformedBody = deformedBody,
            projectedFacesList = projectedFacesList,
            camera = camera,
            showIndexes = showIndexes
        )

        if (isCalculating) CalculatingIndicator(modifier = Modifier.matchParentSize())

        InfoMenu(
            modifier = Modifier.align(Alignment.TopEnd),
            showIndexes = showIndexes,
            toggleIndexesCheckbox = { showIndexes = it })
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

@Composable
private fun RenderBody(
    femBody: FEMBody,
    deformedBody: DoubleArray?,
    projectedFacesList: MutableList<ProjectedFace>,
    camera: SimpleCamera,
    showIndexes: Boolean
) {

    val textMeasurer = rememberTextMeasurer()

    val customFontFamily = FontFamily(Font(Res.font.baloo_bhai_2_regular))

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
private fun SideMenu(
    modifier: Modifier = Modifier,
    sizeX: String,
    sizeY: String,
    sizeZ: String,
    splitX: String,
    splitY: String,
    splitZ: String,
    youngModule: String,
    poissonRatio: String,
    pressure: String,
    bodySize: Vector3,
    bodySplits: Vector3,
    isCalculating: Boolean,
    updateSize: (Vector3) -> Unit,
    updateSplits: (Vector3) -> Unit,
    updateYoungModule: (String) -> Unit,
    updatePoissonRatio: (String) -> Unit,
    updatePressure: (String) -> Unit,
    calculate: () -> Unit
) {

    var showControlPanel by remember { mutableStateOf(true) }

    val interactionSource = remember { MutableInteractionSource() }

    val menuWidth = if (showControlPanel) 220.dp else 40.dp

    val shape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp)

    Row(
        modifier = modifier.width(menuWidth)
            .background(color = VeryLightGray2).fillMaxHeight()
    ) {

        AnimatedVisibility(visible = showControlPanel) {
            ControlPanel(
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
        }

        Box(
            modifier = Modifier
                .padding(top = 40.dp)
                .width(40.dp)
                .height(60.dp)
                .background(
                    color = Color.White,
                    shape = shape
                )
                .clickable(
                    enabled = true,
                    indication = null,
                    interactionSource = interactionSource,
                    onClick = { showControlPanel = !showControlPanel }
                ),
            contentAlignment = Alignment.Center) {

            CustomText(
                text = if (showControlPanel) stringResource(Res.string.left_angle_bracket)
                else stringResource(Res.string.right_angle_bracket),
                fontSize = 25.sp
            )
        }
    }
}

@Composable
private fun ControlPanel(
    sizeX: String,
    sizeY: String,
    sizeZ: String,
    splitX: String,
    splitY: String,
    splitZ: String,
    youngModule: String,
    poissonRatio: String,
    pressure: String,
    bodySize: Vector3,
    bodySplits: Vector3,
    isCalculating: Boolean,
    updateSize: (Vector3) -> Unit,
    updateSplits: (Vector3) -> Unit,
    updateYoungModule: (String) -> Unit,
    updatePoissonRatio: (String) -> Unit,
    updatePressure: (String) -> Unit,
    calculate: () -> Unit
) {

    val labelFontSize = 18.sp

    val shape = RoundedCornerShape(bottomEnd = 8.dp)

    Column(
        modifier = Modifier
            .width(180.dp)
            .background(color = Color.White, shape = shape)
            .verticalScroll(rememberScrollState())
            .padding(6.dp)
            .fillMaxHeight()
    ) {

        BodySizes(
            sizeX = sizeX,
            sizeY = sizeY,
            sizeZ = sizeZ,
            bodySize = bodySize,
            labelFontSize = labelFontSize,
            isCalculating = isCalculating,
            updateSize = updateSize
        )

        Spacer(modifier = Modifier.height(8.dp))

        BodySplits(
            splitX = splitX,
            splitY = splitY,
            splitZ = splitZ,
            labelFontSize = labelFontSize,
            bodySplits = bodySplits,
            isCalculating = isCalculating,
            updateSplits = updateSplits
        )

        Spacer(modifier = Modifier.height(8.dp))

        Parameters(
            youngModule = youngModule,
            poissonRatio = poissonRatio,
            pressure = pressure,
            labelFontSize = labelFontSize,
            isCalculating = isCalculating,
            updateYoungModule = updateYoungModule,
            updatePoissonRatio = updatePoissonRatio,
            updatePressure = updatePressure
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
}

@Composable
private fun BodySizes(
    sizeX: String,
    sizeY: String,
    sizeZ: String,
    bodySize: Vector3,
    labelFontSize: TextUnit,
    isCalculating: Boolean,
    updateSize: (Vector3) -> Unit
) {

    CustomText(
        modifier = Modifier.padding(bottom = 2.dp),
        text = stringResource(Res.string.dimensions),
        fontSize = labelFontSize
    )

    BodySize(
        size = sizeX,
        sizeLabel = stringResource(Res.string.size_x),
        isCalculating = isCalculating,
        updateSize = { updateSize(bodySize.copy(x = it)) }
    )

    BodySize(
        size = sizeY,
        sizeLabel = stringResource(Res.string.size_y),
        isCalculating = isCalculating,
        updateSize = { updateSize(bodySize.copy(y = it)) }
    )

    BodySize(
        size = sizeZ,
        sizeLabel = stringResource(Res.string.size_z),
        isCalculating = isCalculating,
        updateSize = { updateSize(bodySize.copy(z = it)) }
    )
}

@Composable
private fun BodySize(
    size: String,
    sizeLabel: String,
    isCalculating: Boolean,
    updateSize: (Float) -> Unit
) {

    OutlinedTextField(
        value = size,
        onValueChange = {
            it.toFloatOrNull()?.let { v -> updateSize(v) }
        },
        label = { CustomText(text = sizeLabel) },
        singleLine = true,
        enabled = !isCalculating
    )
}

@Composable
private fun BodySplits(
    splitX: String,
    splitY: String,
    splitZ: String,
    labelFontSize: TextUnit,
    bodySplits: Vector3,
    isCalculating: Boolean,
    updateSplits: (Vector3) -> Unit
) {

    CustomText(
        modifier = Modifier.padding(bottom = 2.dp),
        text = stringResource(Res.string.splits),
        fontSize = labelFontSize
    )

    BodySplit(
        split = splitX,
        splitLabel = stringResource(Res.string.split_x),
        isCalculating = isCalculating,
        updateSplit = {
            updateSplits(bodySplits.copy(x = it))
        })

    BodySplit(
        split = splitY,
        splitLabel = stringResource(Res.string.split_y),
        isCalculating = isCalculating,
        updateSplit = {
            updateSplits(bodySplits.copy(y = it))
        })

    BodySplit(
        split = splitZ,
        splitLabel = stringResource(Res.string.split_z),
        isCalculating = isCalculating,
        updateSplit = {
            updateSplits(bodySplits.copy(z = it))
        })
}

@Composable
private fun BodySplit(
    split: String,
    splitLabel: String,
    isCalculating: Boolean,
    updateSplit: (Float) -> Unit
) {
    OutlinedTextField(
        value = split,
        onValueChange = {
            it.toFloatOrNull()?.let { v -> updateSplit(v) }
        },
        label = { CustomText(text = splitLabel) },
        singleLine = true,
        enabled = !isCalculating
    )
}

@Composable
private fun Parameters(
    youngModule: String,
    poissonRatio: String,
    pressure: String,
    labelFontSize: TextUnit,
    isCalculating: Boolean,
    updateYoungModule: (String) -> Unit,
    updatePoissonRatio: (String) -> Unit,
    updatePressure: (String) -> Unit
) {

    CustomText(
        modifier = Modifier.padding(bottom = 2.dp),
        text = stringResource(Res.string.parameters),
        fontSize = labelFontSize
    )

    Parameter(
        parameter = youngModule,
        parameterLabel = stringResource(Res.string.young_module),
        isCalculating = isCalculating,
        updateParameter = updateYoungModule
    )

    Parameter(
        parameter = poissonRatio,
        parameterLabel = stringResource(Res.string.poisson_ratio),
        fontSize = 13.sp,
        isCalculating = isCalculating,
        updateParameter = updatePoissonRatio
    )

    Parameter(
        parameter = pressure,
        parameterLabel = stringResource(Res.string.pressure),
        isCalculating = isCalculating,
        updateParameter = updatePressure
    )
}

@Composable
private fun Parameter(
    parameter: String,
    parameterLabel: String,
    fontSize: TextUnit = 14.sp,
    isCalculating: Boolean,
    updateParameter: (String) -> Unit
) {

    OutlinedTextField(
        value = parameter,
        onValueChange = { updateParameter(it) },
        label = { CustomText(text = parameterLabel, fontSize = fontSize) },
        singleLine = true,
        enabled = !isCalculating
    )
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
fun InfoMenu(
    modifier: Modifier,
    showIndexes: Boolean,
    toggleIndexesCheckbox: (Boolean) -> Unit
) {

    var showPanel by remember { mutableStateOf(true) }

    val interactionSource = remember { MutableInteractionSource() }

    val menuWidth = if (showPanel) 210.dp else 40.dp

    val shape = RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp)

    Row(
        modifier = modifier.width(menuWidth)
            .background(color = VeryLightGray2).fillMaxHeight()
    ) {

        Box(
            modifier = Modifier
                .padding(top = 40.dp)
                .width(40.dp)
                .height(60.dp)
                .background(
                    color = Color.White,
                    shape = shape
                )
                .clickable(
                    enabled = true,
                    indication = null,
                    interactionSource = interactionSource,
                    onClick = { showPanel = !showPanel }
                ),
            contentAlignment = Alignment.Center) {

            CustomText(
                text = if (showPanel) stringResource(Res.string.right_angle_bracket)
                else stringResource(Res.string.left_angle_bracket),
                fontSize = 25.sp
            )
        }

        AnimatedVisibility(visible = showPanel) {
            InfoPanel(
                modifier = modifier,
                showIndexes = showIndexes,
                toggleIndexesCheckbox = toggleIndexesCheckbox
            )
        }
    }
}

@Composable
private fun InfoPanel(
    modifier: Modifier,
    showIndexes: Boolean,
    toggleIndexesCheckbox: (Boolean) -> Unit
) {

    val shape = RoundedCornerShape(bottomStart = 8.dp)

    Column(
        modifier = modifier
            .width(170.dp)
            .background(Color.White.copy(alpha = 0.9f), shape)
            .padding(8.dp)
    ) {
        CustomText(
            modifier = Modifier.padding(bottom = 4.dp),
            text = stringResource(Res.string.axes),
            fontSize = 13.sp,
            color = Color.Gray
        )

        AxisRow(axisName = stringResource(Res.string.axis_x), axisColor = Color.Red)

        AxisRow(axisName = stringResource(Res.string.axis_y), axisColor = Color.Green)

        AxisRow(axisName = stringResource(Res.string.axis_z), axisColor = Color.Blue)

        IndexesCheckbox(
            showIndexes = showIndexes,
            toggleIndexesCheckbox = toggleIndexesCheckbox
        )

        Spacer(modifier = Modifier.height(8.dp))

        FacesInfo()
    }
}

@Composable
private fun AxisRow(axisName: String, axisColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp, 3.dp).background(axisColor))

        Spacer(modifier = Modifier.width(6.dp))

        CustomText(text = axisName)
    }
}

@Composable
fun IndexesCheckbox(showIndexes: Boolean, toggleIndexesCheckbox: (Boolean) -> Unit) {

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 2.dp)
            .width(170.dp)
            .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
            .border(1.dp, VeryLightGray, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Checkbox(
            checked = showIndexes,
            onCheckedChange = { toggleIndexesCheckbox(it) },
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(10.dp))

        CustomText(text = stringResource(Res.string.show_indexes), fontSize = 13.sp)
    }
}

@Composable
fun FacesInfo() {

    val fixedFaceColor = Color.Gray.copy(alpha = 0.7f)

    val loadedFaceColor = Color.Green.copy(alpha = 0.6f)


    FaceInfo(
        color = fixedFaceColor,
        faceType = stringResource(Res.string.fixed_face_type),
        faceInfo = stringResource(Res.string.fixed_face_info)
    )

    Spacer(modifier = Modifier.height(8.dp))

    FaceInfo(
        color = loadedFaceColor,
        faceType = stringResource(Res.string.loaded_face_type),
        faceInfo = stringResource(Res.string.loaded_face_info)
    )
}

@Composable
private fun FaceInfo(color: Color, faceType: String, faceInfo: String) {

    val fontSize = 13.sp

    Row(verticalAlignment = Alignment.CenterVertically) {

        Box(modifier = Modifier.size(20.dp).background(color = color))

        CustomText(
            text = " - $faceType",
            fontSize = fontSize
        )
    }

    Spacer(modifier = Modifier.height(5.dp))

    CustomText(
        text = faceInfo,
        maxLines = 3,
        fontSize = fontSize
    )
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