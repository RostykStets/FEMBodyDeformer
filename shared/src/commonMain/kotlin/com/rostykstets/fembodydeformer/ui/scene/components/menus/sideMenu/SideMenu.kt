package com.rostykstets.fembodydeformer.ui.scene.components.menus.sideMenu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rostykstets.fembodydeformer.ui.models.Vector3
import com.rostykstets.fembodydeformer.ui.scene.components.menus.sideMenu.parameters.Parameters
import com.rostykstets.fembodydeformer.ui.scene.components.menus.sideMenu.sizes.BodySizes
import com.rostykstets.fembodydeformer.ui.scene.components.menus.sideMenu.splits.BodySplits
import com.rostykstets.fembodydeformer.ui.utilities.CustomText
import femlab.shared.generated.resources.Res
import femlab.shared.generated.resources.calculate
import femlab.shared.generated.resources.calculating
import femlab.shared.generated.resources.left_angle_bracket
import femlab.shared.generated.resources.right_angle_bracket
import org.jetbrains.compose.resources.stringResource

@Composable
fun SideMenu(
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
            .background(color = Color.Transparent).fillMaxHeight()
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