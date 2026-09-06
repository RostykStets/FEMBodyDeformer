package com.rostykstets.fembodydeformer.ui.scene.components.menus.infoMenu.axes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rostykstets.fembodydeformer.ui.utilities.CustomText

@Composable
fun AxisRow(axisName: String, axisColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(12.dp, 3.dp).background(axisColor))

        Spacer(modifier = Modifier.width(6.dp))

        CustomText(text = axisName)
    }
}