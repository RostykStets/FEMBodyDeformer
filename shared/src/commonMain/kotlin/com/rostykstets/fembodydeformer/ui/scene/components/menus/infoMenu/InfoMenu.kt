package com.rostykstets.fembodydeformer.ui.scene.components.menus.infoMenu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
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
import com.rostykstets.fembodydeformer.ui.scene.components.menus.infoMenu.axes.AxisRow
import com.rostykstets.fembodydeformer.ui.scene.components.menus.infoMenu.facesInfo.FacesInfo
import com.rostykstets.fembodydeformer.ui.utilities.CustomText
import com.rostykstets.fembodydeformer.ui.utilities.VeryLightGray
import femlab.shared.generated.resources.Res
import femlab.shared.generated.resources.axes
import femlab.shared.generated.resources.axis_x
import femlab.shared.generated.resources.axis_y
import femlab.shared.generated.resources.axis_z
import femlab.shared.generated.resources.drag_scene_info
import femlab.shared.generated.resources.left_angle_bracket
import femlab.shared.generated.resources.right_angle_bracket
import femlab.shared.generated.resources.show_indexes
import femlab.shared.generated.resources.zoom_info
import org.jetbrains.compose.resources.stringResource

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
            .background(color = Color.Transparent).fillMaxHeight()
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
            .verticalScroll(rememberScrollState())
            .padding(8.dp)
            .fillMaxHeight()
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

        Spacer(modifier = Modifier.height(8.dp))

        SceneInteractionInfo()
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
private fun SceneInteractionInfo() {

    val fontSize = 13.sp

    CustomText(
        text = stringResource(Res.string.zoom_info),
        maxLines = 5,
        fontSize = fontSize,
        centerText = false
    )

    Spacer(modifier = Modifier.height(5.dp))

    CustomText(
        text = stringResource(Res.string.drag_scene_info),
        maxLines = 5,
        fontSize = fontSize,
        centerText = false
    )
}