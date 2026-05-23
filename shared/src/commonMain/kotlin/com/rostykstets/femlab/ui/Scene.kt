package com.rostykstets.femlab.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isSecondaryPressed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rostykstets.femlab.ui.components.PureBlue
import com.rostykstets.femlab.ui.components.VeryDarkGray
import com.rostykstets.femlab.ui.components.VeryLightGray
import com.rostykstets.femlab.ui.components.VeryLightGray2
import femlab.shared.generated.resources.Res
import femlab.shared.generated.resources.axes
import femlab.shared.generated.resources.axis_x
import femlab.shared.generated.resources.axis_y
import femlab.shared.generated.resources.axis_z
import femlab.shared.generated.resources.baloo_bhai_2_regular
import femlab.shared.generated.resources.dimensions
import femlab.shared.generated.resources.parameters
import femlab.shared.generated.resources.poisson_ratio
import femlab.shared.generated.resources.pressure
import femlab.shared.generated.resources.size_z
import femlab.shared.generated.resources.size_x
import femlab.shared.generated.resources.size_y
import femlab.shared.generated.resources.split_z
import femlab.shared.generated.resources.split_x
import femlab.shared.generated.resources.split_y
import femlab.shared.generated.resources.splits
import femlab.shared.generated.resources.young_module
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.stringResource

@Composable
fun Scene() {

    var bodySize by remember { mutableStateOf(Vector3(4f, 4f, 4f)) }
    var bodySplits by remember { mutableStateOf(Vector3(2f, 2f, 2f)) }

    var sizeXText by remember { mutableStateOf("4") }
    var sizeYText by remember { mutableStateOf("4") }
    var sizeZText by remember { mutableStateOf("4") }

    var splitXText by remember { mutableStateOf("2") }
    var splitYText by remember { mutableStateOf("2") }
    var splitZText by remember { mutableStateOf("2") }

    var youngModule by remember { mutableStateOf("4") }
    var poissonRatio by remember { mutableStateOf("4") }
    var pressure by remember { mutableStateOf("4") }

    val bodyPoints = remember(bodySize, bodySplits) { buildBodyShape(bodySize, bodySplits) }
    val camera = remember { SimpleCamera() }

    val textMeasurer = rememberTextMeasurer()
    val customFontFamily = FontFamily(Font(Res.font.baloo_bhai_2_regular))

    Row(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .width(180.dp)
                .background(color = Color.White)
                .verticalScroll(rememberScrollState())
                .padding(6.dp)
                .fillMaxHeight()
        ) {
            Text(
                text = stringResource(Res.string.dimensions),
                modifier = Modifier.padding(bottom = 2.dp)
            )
            OutlinedTextField(
                value = sizeXText,
                onValueChange = {
                    sizeXText = it; it.toFloatOrNull()?.let { v -> bodySize = bodySize.copy(x = v) }
                },
                label = { Text(stringResource(Res.string.size_x)) },
                singleLine = true
            )
            OutlinedTextField(
                value = sizeYText,
                onValueChange = {
                    sizeYText = it; it.toFloatOrNull()?.let { v -> bodySize = bodySize.copy(y = v) }
                },
                label = { Text(stringResource(Res.string.size_y)) },
                singleLine = true
            )
            OutlinedTextField(
                value = sizeZText,
                onValueChange = {
                    sizeZText = it; it.toFloatOrNull()?.let { v -> bodySize = bodySize.copy(z = v) }
                },
                label = { Text(stringResource(Res.string.size_z)) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.splits),
                modifier = Modifier.padding(bottom = 2.dp)
            )
            OutlinedTextField(
                value = splitXText,
                onValueChange = {
                    splitXText = it; it.toFloatOrNull()
                    ?.let { v -> bodySplits = bodySplits.copy(x = v) }
                },
                label = { Text(stringResource(Res.string.split_x)) },
                singleLine = true
            )
            OutlinedTextField(
                value = splitYText,
                onValueChange = {
                    splitYText = it; it.toFloatOrNull()
                    ?.let { v -> bodySplits = bodySplits.copy(y = v) }
                },
                label = { Text(stringResource(Res.string.split_y)) },
                singleLine = true
            )
            OutlinedTextField(
                value = splitZText,
                onValueChange = {
                    splitZText = it; it.toFloatOrNull()
                    ?.let { v -> bodySplits = bodySplits.copy(z = v) }
                },
                label = { Text(stringResource(Res.string.split_z)) },
                singleLine = true
            )
            Text(
                text = stringResource(Res.string.parameters),
                modifier = Modifier.padding(bottom = 2.dp)
            )
            OutlinedTextField(
                value = youngModule,
                onValueChange = {
                    youngModule = it
                },
                label = { Text(stringResource(Res.string.young_module)) },
                singleLine = true
            )
            OutlinedTextField(
                value = poissonRatio,
                onValueChange = {
                    poissonRatio = it
                },
                label = { Text(stringResource(Res.string.poisson_ratio)) },
                singleLine = true
            )
            OutlinedTextField(
                value = pressure,
                onValueChange = {
                    pressure = it
                },
                label = { Text(stringResource(Res.string.pressure)) },
                singleLine = true
            )
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .weight(1f)
                .background(VeryLightGray2)
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            val fingersCount = event.changes.size

                            if (event.type == PointerEventType.Move) {
                                val change = event.changes.firstOrNull()
                                if (change != null) {
                                    val dragAmount = change.position - change.previousPosition

                                    val isRightClick = event.buttons.isSecondaryPressed
                                    val isThreeFingers = fingersCount >= 3

                                    if (isThreeFingers || isRightClick) {
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
                                                val zoomRatio = currDistance / prevDistance
                                                camera.scaleModifier =
                                                    (camera.scaleModifier * zoomRatio).coerceIn(
                                                        0.3f,
                                                        5.0f
                                                    )
                                            }
                                        }
                                    } else if (fingersCount == 1 && !isRightClick) {
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
                            if (event.type == PointerEventType.Scroll) {
                                val change = event.changes.firstOrNull()
                                if (change != null) {
                                    val rawY = change.scrollDelta.y
                                    if (rawY != 0f) {
                                        val zoomFactor = kotlin.math.exp(-rawY * 0.05f)
                                        camera.scaleModifier =
                                            (camera.scaleModifier * zoomFactor).coerceIn(0.3f, 5.0f)
                                        change.consume()
                                    }
                                }
                            }
                        }
                    }
                }
                .pointerInput(Unit) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            if (event.type == PointerEventType.Scroll) {
                                val change = event.changes.firstOrNull()
                                if (change != null) {
                                    val rawY = change.scrollDelta.y
                                    if (rawY != 0f) {
                                        val zoomFactor = kotlin.math.exp(-rawY * 0.05f)
                                        camera.scaleModifier =
                                            (camera.scaleModifier * zoomFactor).coerceIn(0.3f, 5.0f)
                                        change.consume()
                                    }
                                }
                            }
                        }
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                if (w <= 0f || h <= 0f) return@Canvas

                val baseScale = minOf(w, h) / 12f

                val p0 = camera.project(Vector3(0f, 0f, 0f), w, h, baseScale)
                val pX = camera.project(Vector3(2f, 0f, 0f), w, h, baseScale)
                val pY = camera.project(Vector3(0f, 2f, 0f), w, h, baseScale)
                val pZ = camera.project(Vector3(0f, 0f, 2f), w, h, baseScale)

                drawLine(Color.Red, p0, pX, strokeWidth = 4f)
                drawLine(Color.Green, p0, pY, strokeWidth = 4f)
                drawLine(Color.Blue, p0, pZ, strokeWidth = 4f)

                drawWireCube(bodySize, camera, w, h, baseScale)

                bodyPoints.forEachIndexed { index, vertex ->
                    val screenPt = camera.project(vertex, w, h, baseScale)

                    drawCircle(
                        color = PureBlue,
                        radius = 4.5f,
                        center = screenPt
                    )

                    drawText(
                        textMeasurer = textMeasurer,
                        text = "${index + 1}",
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
            }

            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(8.dp))
                    .border(1.dp, VeryLightGray, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Text(
                    stringResource(Res.string.axes),
                    style = TextStyle(fontSize = 11.sp, color = Color.Gray),
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Box(modifier = Modifier.size(12.dp, 3.dp).background(Color.Red))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        stringResource(Res.string.axis_x),
                        style = TextStyle(fontSize = 12.sp, fontFamily = customFontFamily)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Box(modifier = Modifier.size(12.dp, 3.dp).background(Color.Green))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        stringResource(Res.string.axis_y),
                        style = TextStyle(fontSize = 12.sp, fontFamily = customFontFamily)
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Box(modifier = Modifier.size(12.dp, 3.dp).background(Color.Blue))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        stringResource(Res.string.axis_z),
                        style = TextStyle(fontSize = 12.sp, fontFamily = customFontFamily)
                    )
                }
            }
        }
    }
}

fun DrawScope.drawWireCube(
    size: Vector3,
    camera: SimpleCamera,
    w: Float,
    h: Float,
    baseScale: Float
) {
    val hX = size.x / 2f
    val hY = size.y
    val hZ = size.z / 2f

    val vertices = listOf(
        Vector3(-hX, 0f, -hZ), Vector3(hX, 0f, -hZ), Vector3(hX, 0f, hZ), Vector3(-hX, 0f, hZ),
        Vector3(-hX, hY, -hZ), Vector3(hX, hY, -hZ), Vector3(hX, hY, hZ), Vector3(-hX, hY, hZ)
    )
    val projected = vertices.map { camera.project(it, w, h, baseScale) }

    val edges = listOf(
        0 to 1, 1 to 2, 2 to 3, 3 to 0,
        4 to 5, 5 to 6, 6 to 7, 7 to 4,
        0 to 4, 1 to 5, 2 to 6, 3 to 7
    )

    edges.forEach { (from, to) ->
        drawLine(
            color = VeryDarkGray,
            start = projected[from],
            end = projected[to],
            strokeWidth = 2f
        )
    }
}