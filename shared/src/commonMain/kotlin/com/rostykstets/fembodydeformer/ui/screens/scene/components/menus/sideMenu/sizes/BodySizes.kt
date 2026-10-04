package com.rostykstets.fembodydeformer.ui.screens.scene.components.menus.sideMenu.sizes

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.rostykstets.fembodydeformer.ui.models.Vector3
import com.rostykstets.fembodydeformer.ui.screens.utilities.CustomText
import femlab.shared.generated.resources.Res
import femlab.shared.generated.resources.dimensions
import femlab.shared.generated.resources.size_x
import femlab.shared.generated.resources.size_y
import femlab.shared.generated.resources.size_z
import org.jetbrains.compose.resources.stringResource

@Composable
fun BodySizes(
    bodySize: Vector3,
    labelFontSize: TextUnit,
    isCalculating: Boolean,
    updateSize: (Vector3) -> Unit
) {

    val (sizeX, sizeY, sizeZ) = bodySize

    CustomText(
        modifier = Modifier.padding(bottom = 2.dp),
        text = stringResource(Res.string.dimensions),
        fontSize = labelFontSize
    )

    BodySize(
        size = sizeX.toString(),
        sizeLabel = stringResource(Res.string.size_x),
        isCalculating = isCalculating,
        updateSize = { updateSize(bodySize.copy(x = it)) }
    )

    BodySize(
        size = sizeY.toString(),
        sizeLabel = stringResource(Res.string.size_y),
        isCalculating = isCalculating,
        updateSize = { updateSize(bodySize.copy(y = it)) }
    )

    BodySize(
        size = sizeZ.toString(),
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