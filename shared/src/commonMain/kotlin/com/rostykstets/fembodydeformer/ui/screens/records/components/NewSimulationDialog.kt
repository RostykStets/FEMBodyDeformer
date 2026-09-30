package com.rostykstets.fembodydeformer.ui.screens.records.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rostykstets.fembodydeformer.ui.screens.utilities.CustomText
import com.rostykstets.fembodydeformer.ui.screens.utilities.DarkRed
import com.rostykstets.fembodydeformer.ui.screens.utilities.SCREEN_HEIGHT_DESKTOP
import com.rostykstets.fembodydeformer.ui.screens.utilities.SCREEN_HEIGHT_MOBILE
import com.rostykstets.fembodydeformer.ui.screens.utilities.SCREEN_WIDTH_DESKTOP
import com.rostykstets.fembodydeformer.ui.screens.utilities.SCREEN_WIDTH_MOBILE
import com.rostykstets.fembodydeformer.ui.screens.utilities.VeryDarkGray
import com.rostykstets.fembodydeformer.ui.screens.utilities.VeryLightGray
import femlab.shared.generated.resources.Res
import femlab.shared.generated.resources.cancel
import femlab.shared.generated.resources.create
import femlab.shared.generated.resources.create_new_project
import femlab.shared.generated.resources.project_title_label
import femlab.shared.generated.resources.type_project_title
import org.jetbrains.compose.resources.stringResource

@Composable
fun NewSimulationRecordDialog(onDismiss: () -> Unit, onCreateRecord: (String) -> Unit) {

    val interactionSource = remember { MutableInteractionSource() }

    val dialogWidth = 400.dp
    val dialogHeight = 300.dp

    var newProjectTitle by remember { mutableStateOf("") }

    val shape = RoundedCornerShape(20.dp)

    val textFieldWidth = 300.dp
    val textFieldHeight = 60.dp

    Box(
        modifier = Modifier.fillMaxSize()
            .background(color = VeryDarkGray.copy(alpha = 0.3f))
            .clickable(
                enabled = true,
                indication = null,
                interactionSource = interactionSource,
                onClick = onDismiss
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.background(color = VeryLightGray, shape = shape)
                .size(width = dialogWidth, height = dialogHeight)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(0.05f))

            CustomText(text = stringResource(Res.string.create_new_project), fontSize = 22.sp)

            Spacer(modifier = Modifier.weight(0.2f))

            CustomText(text = stringResource(Res.string.type_project_title), fontSize = 18.sp)

            Spacer(modifier = Modifier.weight(0.05f))

            OutlinedTextField(
                modifier = Modifier.size(width = textFieldWidth, height = textFieldHeight),
                label = {
                    CustomText(
                        text = stringResource(Res.string.project_title_label),
                        fontSize = 15.sp
                    )
                },
                value = newProjectTitle,
                singleLine = true,
                onValueChange = { newProjectTitle = it },
                textStyle = TextStyle.Default.copy(fontSize = 15.sp)
            )

            Spacer(modifier = Modifier.weight(0.3f))

            Row(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.3f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                DialogButton(
                    text = stringResource(Res.string.cancel),
                    interactionSource = interactionSource,
                    onClick = onDismiss
                )

                DialogButton(
                    text = stringResource(Res.string.create),
                    interactionSource = interactionSource,
                    onClick = { onCreateRecord(newProjectTitle) }
                )
            }

            Spacer(modifier = Modifier.weight(0.2f))
        }
    }
}

@Composable
private fun DialogButton(
    text: String,
    interactionSource: MutableInteractionSource,
    onClick: () -> Unit
) {

    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = Modifier
            .size(width = 100.dp, height = 50.dp)
            .background(color = DarkRed, shape = shape)
            .clickable(
                enabled = true,
                indication = null,
                interactionSource = interactionSource,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        CustomText(text = text, color = Color.White)
    }
}

@Preview(
    widthDp = SCREEN_WIDTH_DESKTOP,
    heightDp = SCREEN_HEIGHT_DESKTOP
)
@Composable
private fun NewSimulationDialogDesktopPreview() {
    NewSimulationRecordDialog(onDismiss = {}, onCreateRecord = {})
}

@Preview(
    widthDp = SCREEN_WIDTH_MOBILE,
    heightDp = SCREEN_HEIGHT_MOBILE
)
@Composable
private fun NewSimulationDialogMobilePreview() {
    NewSimulationRecordDialog(onDismiss = {}, onCreateRecord = {})
}