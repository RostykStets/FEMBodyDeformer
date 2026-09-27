package com.rostykstets.fembodydeformer.ui.screens.scene.components.menus.sideMenu.parameters

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rostykstets.fembodydeformer.ui.screens.utilities.CustomText
import femlab.shared.generated.resources.Res
import femlab.shared.generated.resources.parameters
import femlab.shared.generated.resources.poisson_ratio
import femlab.shared.generated.resources.pressure
import femlab.shared.generated.resources.young_module
import org.jetbrains.compose.resources.stringResource

@Composable
fun Parameters(
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