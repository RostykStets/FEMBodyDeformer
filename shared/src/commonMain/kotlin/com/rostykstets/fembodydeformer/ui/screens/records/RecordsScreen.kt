package com.rostykstets.fembodydeformer.ui.screens.records

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import com.rostykstets.fembodydeformer.domain.models.SimulationRecord
import com.rostykstets.fembodydeformer.ui.navigation.localNavController
import com.rostykstets.fembodydeformer.ui.routing.SceneScreen
import com.rostykstets.fembodydeformer.ui.screens.records.components.NewSimulationRecordDialog
import com.rostykstets.fembodydeformer.ui.screens.records.components.RecordItem
import com.rostykstets.fembodydeformer.ui.screens.records.viewmodels.RecordsViewModel
import com.rostykstets.fembodydeformer.ui.screens.utilities.CustomText
import com.rostykstets.fembodydeformer.ui.screens.utilities.SCREEN_HEIGHT_DESKTOP
import com.rostykstets.fembodydeformer.ui.screens.utilities.SCREEN_HEIGHT_MOBILE
import com.rostykstets.fembodydeformer.ui.screens.utilities.SCREEN_WIDTH_DESKTOP
import com.rostykstets.fembodydeformer.ui.screens.utilities.SCREEN_WIDTH_MOBILE
import com.rostykstets.fembodydeformer.ui.screens.utilities.VeryLightGray
import femlab.shared.generated.resources.Res
import femlab.shared.generated.resources.no_records
import femlab.shared.generated.resources.records
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.time.Clock

@Composable
fun RecordsScreen() {

    val navController = localNavController.current

    val recordsViewModel: RecordsViewModel = koinViewModel()

    val records by recordsViewModel.records.collectAsState()
    val isLoading by recordsViewModel.isLoading.collectAsState()

    val isSavingRecord by recordsViewModel.isSavingRecord.collectAsState()

    var showDialog by remember { mutableStateOf(false) }

    fun navigateToScene(id: Int) {
        navController.navigate(route = SceneScreen(id = id)) {
            launchSingleTop = true
        }
    }

    RecordsContent(
        records = records,
        isLoading = isLoading,
        openRecord = { id -> navigateToScene(id) },
        addRecord = { showDialog = true },
        onBack = {})

    AnimatedVisibility(
        visible = showDialog,
        enter = EnterTransition.None,
        exit = ExitTransition.None
    ) {
        NewSimulationRecordDialog(
            isSavingRecord = isSavingRecord,
            onDismiss = { showDialog = false },
            onCreateRecord = { title ->
                recordsViewModel.createRecord(title, onCreateRecord = { id ->
                    navigateToScene(id)
                })
            }
        )
    }
}

@Composable
private fun RecordsContent(
    records: List<SimulationRecord>,
    isLoading: Boolean,
    openRecord: (id: Int) -> Unit,
    addRecord: () -> Unit,
    onBack: () -> Unit
) {

    NavigationBackHandler(
        rememberNavigationEventState(NavigationEventInfo.None),
        onBackCompleted = onBack
    )

    Column(
        modifier = Modifier.fillMaxSize().background(color = VeryLightGray),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.weight(0.2f))

        TopBar(onBack = onBack, createRecord = addRecord)

        Spacer(modifier = Modifier.weight(0.2f))

        if (isLoading) {
            Box(
                modifier = Modifier.weight(2f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(40.dp))
            }
        } else if (records.isEmpty()) {
            Column(
                modifier = Modifier.weight(2f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.weight(0.5f))

                CustomText(text = stringResource(Res.string.no_records), fontSize = 25.sp)

                Spacer(modifier = Modifier.weight(1f))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(2f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(30.dp)
            ) {
                items(records) { record ->
                    RecordItem(record, onClick = openRecord)
                }
            }
        }
    }
}

@Composable
private fun TopBar(
    onBack: () -> Unit,
    createRecord: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 40.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clickable(
                    enabled = true,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onBack
                )
        )

        CustomText(text = stringResource(Res.string.records), fontSize = 28.sp)

        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clickable(
                    enabled = true,
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = createRecord
                )
        )
    }
}

@Preview(
    widthDp = SCREEN_WIDTH_DESKTOP,
    heightDp = SCREEN_HEIGHT_DESKTOP
)
@Composable
private fun RecordsDesktopPreview() {

    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    RecordsContent(
        records = listOf(
            SimulationRecord(
                id = 0,
                title = "Today",
                dateAdded = now,
                dateModified = now,
                sizeX = 0f,
                sizeY = 0f,
                sizeZ = 0f,
                splitX = 0,
                splitY = 0,
                splitZ = 0,
                youngModule = 0.0,
                poissonRatio = 0.0,
                pressure = 0.0,
                fixedFaces = emptyList(),
                loadedFaces = emptyList(),
                isBodyDeformed = false
            ),
            SimulationRecord(
                id = 0,
                title = "Old",
                dateAdded = now,
                dateModified = LocalDateTime(2024, 5, 3, 15, 32, 9),
                sizeX = 0f,
                sizeY = 0f,
                sizeZ = 0f,
                splitX = 0,
                splitY = 0,
                splitZ = 0,
                youngModule = 0.0,
                poissonRatio = 0.0,
                pressure = 0.0,
                fixedFaces = emptyList(),
                loadedFaces = emptyList(),
                isBodyDeformed = false
            )
        ),
        isLoading = false,
        openRecord = {},
        addRecord = {},
        onBack = {}
    )
}

@Preview(
    widthDp = SCREEN_WIDTH_MOBILE,
    heightDp = SCREEN_HEIGHT_MOBILE
)
@Composable
private fun RecordsMobilePreview() {

    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    RecordsContent(
        records = listOf(
            SimulationRecord(
                id = 0,
                title = "Today",
                dateAdded = now,
                dateModified = now,
                sizeX = 0f,
                sizeY = 0f,
                sizeZ = 0f,
                splitX = 0,
                splitY = 0,
                splitZ = 0,
                youngModule = 0.0,
                poissonRatio = 0.0,
                pressure = 0.0,
                fixedFaces = emptyList(),
                loadedFaces = emptyList(),
                isBodyDeformed = false
            ),
            SimulationRecord(
                id = 0,
                title = "Old",
                dateAdded = now,
                dateModified = LocalDateTime(2024, 5, 3, 15, 32, 9),
                sizeX = 0f,
                sizeY = 0f,
                sizeZ = 0f,
                splitX = 0,
                splitY = 0,
                splitZ = 0,
                youngModule = 0.0,
                poissonRatio = 0.0,
                pressure = 0.0,
                fixedFaces = emptyList(),
                loadedFaces = emptyList(),
                isBodyDeformed = false
            )
        ),
        isLoading = false,
        openRecord = {},
        addRecord = {},
        onBack = {}
    )
}