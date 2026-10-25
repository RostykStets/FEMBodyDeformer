package com.rostykstets.fembodydeformer.ui.screens.records.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rostykstets.fembodydeformer.ui.screens.utilities.CustomButton
import com.rostykstets.fembodydeformer.ui.screens.utilities.CustomText
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
fun NewSimulationRecordDialog(
    isSavingRecord: Boolean,
    onDismiss: () -> Unit,
    onCreateRecord: (String) -> Unit
) {

    val dialogWidth = 400.dp
    val dialogHeight = 300.dp

    var newProjectTitle by remember { mutableStateOf("") }

    val shape = RoundedCornerShape(20.dp)

    val textFieldWidth = 300.dp
    val textFieldHeight = 60.dp

    Box(
        modifier = Modifier.fillMaxSize()
            .background(color = VeryDarkGray.copy(alpha = 0.3f))
            .pointerInput(Unit) {
                detectTapGestures(onTap = { onDismiss() })
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .size(width = dialogWidth, height = dialogHeight)
                .background(color = VeryLightGray, shape = shape)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {})
                }
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
                CustomButton(
                    text = stringResource(Res.string.cancel),
                    enabled = !isSavingRecord,
                    width = 100.dp,
                    height = 50.dp,
                    onClick = onDismiss
                )

                CustomButton(
                    text = stringResource(Res.string.create),
                    enabled = !isSavingRecord,
                    width = 100.dp,
                    height = 50.dp,
                    onClick = { onCreateRecord(newProjectTitle) }
                )
            }

            Spacer(modifier = Modifier.weight(0.2f))
        }

        if (isSavingRecord) {
            Box(
                modifier = Modifier.fillMaxSize()
                    .background(color = VeryDarkGray.copy(alpha = 0.5f))
                    .clickable(
                        enabled = false,
                        onClick = {}
                    ),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(40.dp), color = Color.Magenta)
            }
        }
    }
}

@Preview(
    widthDp = SCREEN_WIDTH_DESKTOP,
    heightDp = SCREEN_HEIGHT_DESKTOP
)
@Composable
private fun NewSimulationDialogDesktopPreview() {
    NewSimulationRecordDialog(
        isSavingRecord = true,
        onDismiss = {},
        onCreateRecord = {})
}

@Preview(
    widthDp = SCREEN_WIDTH_MOBILE,
    heightDp = SCREEN_HEIGHT_MOBILE
)
@Composable
private fun NewSimulationDialogMobilePreview() {
    NewSimulationRecordDialog(
        isSavingRecord = false,
        onDismiss = {},
        onCreateRecord = {})
}