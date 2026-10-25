package com.rostykstets.fembodydeformer.ui.screens.scene.components.menus.projectMenu

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rostykstets.fembodydeformer.ui.screens.utilities.CustomButton
import com.rostykstets.fembodydeformer.ui.screens.utilities.CustomText
import femlab.shared.generated.resources.Res
import femlab.shared.generated.resources.open
import femlab.shared.generated.resources.save
import org.jetbrains.compose.resources.stringResource

@Composable
fun ProjectMenu(
    modifier: Modifier,
    projectTitle: String,
    onSave: () -> Unit,
    onOpenProject: () -> Unit
) {

    var showMenu by remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }

    val toggleHeight = 40.dp
    val toggleWidth = 60.dp

    val menuHeight = 60.dp

    val height = if (showMenu) menuHeight + toggleHeight else toggleHeight

    val shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)

    Column(
        modifier = modifier.height(height)
            .background(color = Color.Transparent).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(visible = showMenu) {
            Row(
                modifier = modifier
                    .height(menuHeight)
                    .background(Color.White)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = BiasAlignment.Horizontal(-0.6f),
                    verticalArrangement = Arrangement.Center
                ) {
                    CustomText(
                        text = projectTitle,
                        fontSize = 23.sp
                    )
                }

                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    CustomButton(
                        text = stringResource(Res.string.save),
                        width = 100.dp,
                        onClick = onSave
                    )

                    CustomButton(
                        text = stringResource(Res.string.open),
                        width = 100.dp,
                        onClick = onOpenProject
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .width(toggleWidth)
                .height(toggleHeight)
                .background(
                    color = Color.White,
                    shape = shape
                )
                .clickable(
                    enabled = true,
                    indication = null,
                    interactionSource = interactionSource,
                    onClick = { showMenu = !showMenu }
                ),
            contentAlignment = Alignment.Center) {
            Icon(
                imageVector = if (showMenu) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = null,
                modifier = Modifier.matchParentSize()
            )
        }
    }
}