package com.rostykstets.fembodydeformer.ui.screens.records.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rostykstets.fembodydeformer.domain.models.SimulationRecord
import com.rostykstets.fembodydeformer.ui.screens.utilities.CustomText
import com.rostykstets.fembodydeformer.ui.screens.utilities.DarkRed
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

@Composable
fun RecordItem(simulationRecord: SimulationRecord, onClick: (SimulationRecord) -> Unit) {

    val rowWidthFraction = 0.7f
    val rowHeightFraction = 0.07f

    val rowShape = RoundedCornerShape(16.dp)

    val fontSize = 20.sp

    Row(
        modifier = Modifier
            .fillMaxWidth(rowWidthFraction)
            .aspectRatio(rowWidthFraction / rowHeightFraction)
            .background(color = DarkRed, shape = rowShape)
            .padding(horizontal = 25.dp)
            .clickable(
                enabled = true,
                indication = null,
                interactionSource = remember { MutableInteractionSource() },
                onClick = { onClick(simulationRecord) }),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CustomText(
            text = simulationRecord.title,
            fontSize = fontSize,
            color = Color.White
        )

        CustomText(
            text = simulationRecord.dateModified.formatDateModified(),
            fontSize = fontSize,
            color = Color.White
        )
    }
}

private fun LocalDateTime.formatDateModified(): String {

    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())

    val year = if (this.year < now.year) "${this.year} року" else ""

    val month = parseMonth(this.month)

    val day = this.day

    val isToday = (now.date == this.date)

    return if (isToday) "Сьогодні" else "$day $month $year"
}

private fun parseMonth(month: Month): String {
    return when (month) {
        Month.JANUARY -> "січня"
        Month.FEBRUARY -> "лютого"
        Month.MARCH -> "березня"
        Month.APRIL -> "квітня"
        Month.MAY -> "травня"
        Month.JUNE -> "червня"
        Month.JULY -> "липня"
        Month.AUGUST -> "серпня"
        Month.SEPTEMBER -> "вересня"
        Month.OCTOBER -> "жовтня"
        Month.NOVEMBER -> "листопада"
        Month.DECEMBER -> "грудня"
    }
}