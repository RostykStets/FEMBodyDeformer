package com.rostykstets.fembodydeformer.ui.scene

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
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rostykstets.fembodydeformer.domain.models.ElementFace
import com.rostykstets.fembodydeformer.domain.models.FEMBody
import com.rostykstets.fembodydeformer.domain.models.ProjectedFace
import com.rostykstets.fembodydeformer.ui.utilities.CustomText
import com.rostykstets.fembodydeformer.ui.utilities.VeryLightGray2
import com.rostykstets.fembodydeformer.ui.models.SimpleCamera
import com.rostykstets.fembodydeformer.ui.models.Vector3
import com.rostykstets.fembodydeformer.ui.scene.components.body.RenderBody
import com.rostykstets.fembodydeformer.ui.scene.components.menus.infoMenu.InfoMenu
import com.rostykstets.fembodydeformer.ui.scene.components.menus.sideMenu.SideMenu
import com.rostykstets.fembodydeformer.ui.viewmodels.FEMViewModel
import femlab.shared.generated.resources.Res
import femlab.shared.generated.resources.warning_message
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