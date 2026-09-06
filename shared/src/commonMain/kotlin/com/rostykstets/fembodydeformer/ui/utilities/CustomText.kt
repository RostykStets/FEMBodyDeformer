package com.rostykstets.fembodydeformer.ui.utilities

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import femlab.shared.generated.resources.Res
import femlab.shared.generated.resources.baloo_bhai_2_regular
import org.jetbrains.compose.resources.Font

@Composable
fun CustomText(
    modifier: Modifier = Modifier,
    text: String,
    fontSize: TextUnit = 14.sp,
    color: Color = Color.Black,
    maxLines: Int = 1,
    centerText: Boolean = true
) {

    val customFontFamily = FontFamily(Font(Res.font.baloo_bhai_2_regular))

    Text(
        modifier = modifier,
        text = text,
        style = TextStyle(fontSize = fontSize, color = color, fontFamily = customFontFamily),
        maxLines = maxLines,
        textAlign = if(centerText) TextAlign.Center else TextAlign.Start
    )
}