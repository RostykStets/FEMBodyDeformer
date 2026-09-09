package com.rostykstets.fembodydeformer.ui.scene.components.menus.infoMenu.facesInfo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.rostykstets.fembodydeformer.ui.utilities.CustomText
import femlab.shared.generated.resources.Res
import femlab.shared.generated.resources.fixed_face_info
import femlab.shared.generated.resources.fixed_face_type
import femlab.shared.generated.resources.loaded_face_info
import femlab.shared.generated.resources.loaded_face_type
import org.jetbrains.compose.resources.stringResource

@Composable
fun FacesInfo() {

    val fixedFaceColor = Color.Gray.copy(alpha = 0.7f)

    val loadedFaceColor = Color.Green.copy(alpha = 0.6f)


    FaceInfo(
        color = fixedFaceColor,
        faceType = stringResource(Res.string.fixed_face_type),
        faceInfo = stringResource(Res.string.fixed_face_info)
    )

    Spacer(modifier = Modifier.height(8.dp))

    FaceInfo(
        color = loadedFaceColor,
        faceType = stringResource(Res.string.loaded_face_type),
        faceInfo = stringResource(Res.string.loaded_face_info)
    )
}

@Composable
private fun FaceInfo(color: Color, faceType: String, faceInfo: String) {

    Row(verticalAlignment = Alignment.CenterVertically) {

        Box(modifier = Modifier.size(20.dp).background(color = color))

        CustomText(text = " - $faceType")
    }

    Spacer(modifier = Modifier.height(5.dp))

    CustomText(
        text = faceInfo,
        maxLines = 3,
    )
}