package com.rostykstets.fembodydeformer.ui.screens.scene.components.menus.sideMenu.splits

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.rostykstets.fembodydeformer.ui.models.Vector3
import com.rostykstets.fembodydeformer.ui.screens.utilities.CustomText
import femlab.shared.generated.resources.Res
import femlab.shared.generated.resources.split_x
import femlab.shared.generated.resources.split_y
import femlab.shared.generated.resources.split_z
import femlab.shared.generated.resources.splits
import org.jetbrains.compose.resources.stringResource

@Composable
fun BodySplits(
    labelFontSize: TextUnit,
    bodySplits: Vector3,
    isCalculating: Boolean,
    updateSplits: (Vector3) -> Unit
) {

    val (splitX, splitY, splitZ) = bodySplits

    CustomText(
        modifier = Modifier.padding(bottom = 2.dp),
        text = stringResource(Res.string.splits),
        fontSize = labelFontSize
    )

    BodySplit(
        split = splitX.toString(),
        splitLabel = stringResource(Res.string.split_x),
        isCalculating = isCalculating,
        updateSplit = {
            updateSplits(bodySplits.copy(x = it))
        })

    BodySplit(
        split = splitY.toString(),
        splitLabel = stringResource(Res.string.split_y),
        isCalculating = isCalculating,
        updateSplit = {
            updateSplits(bodySplits.copy(y = it))
        })

    BodySplit(
        split = splitZ.toString(),
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