package com.rostykstets.fembodydeformer.ui.screens.utilities

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun CustomButton(
    text: String,
    width: Dp = 150.dp,
    height: Dp = 40.dp,
    onClick: () -> Unit
) {

    val shape = RoundedCornerShape(16.dp)

    Button(
        onClick = onClick,
        interactionSource = remember { MutableInteractionSource() },
        colors = ButtonColors(
            containerColor = DarkRed,
            contentColor = DarkRed,
            disabledContentColor = DarkRed,
            disabledContainerColor = DarkRed
        ),
        shape = shape,
        modifier = Modifier.width(width).height(height),
        contentPadding = PaddingValues(0.dp)
    ) {
        CustomText(text = text, color = Color.White)
    }
}