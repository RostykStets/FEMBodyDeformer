package com.rostykstets.fembodydeformer.data.mappers

import com.rostykstets.fembodydeformer.data.entities.ElementFaceEntity
import com.rostykstets.fembodydeformer.data.entities.SimulationRecordEntity
import com.rostykstets.fembodydeformer.domain.models.ElementFace
import com.rostykstets.fembodydeformer.domain.models.SimulationRecord
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

fun SimulationRecordEntity.toModel(): SimulationRecord {
    return SimulationRecord(
        id = this.id,
        dateAdded = this.dateAdded.toLocalDateTime(),
        dateModified = this.dateModified.toLocalDateTime(),
        sizeX = this.sizeX,
        sizeY = this.sizeY,
        sizeZ = this.sizeZ,
        splitX = this.splitX,
        splitY = this.splitY,
        splitZ = this.splitZ,
        youngModule = this.youngModule,
        poissonRatio = this.poissonRatio,
        pressure = this.pressure,
        fixedFaces = this.fixedFaces.toModel(),
        loadedFaces = this.loadedFaces.toModel(),
        isBodyDeformed = this.isBodyDeformed
    )
}

fun SimulationRecord.toEntity(): SimulationRecordEntity {

    return SimulationRecordEntity(
        id = this.id,
        dateAdded = this.dateAdded.toLong(),
        dateModified = this.dateModified.toLong(),
        sizeX = this.sizeX,
        sizeY = this.sizeY,
        sizeZ = this.sizeZ,
        splitX = this.splitX,
        splitY = this.splitY,
        splitZ = this.splitZ,
        youngModule = this.youngModule,
        poissonRatio = this.poissonRatio,
        pressure = this.pressure,
        fixedFaces = this.fixedFaces.toEntity(),
        loadedFaces = this.loadedFaces.toEntity(),
        isBodyDeformed = this.isBodyDeformed
    )
}

private fun Long.toLocalDateTime(): LocalDateTime {

    val instant = Instant.fromEpochMilliseconds(this)

    return instant.toLocalDateTime(TimeZone.currentSystemDefault())
}

private fun LocalDateTime.toLong(): Long {
    return this.toInstant(timeZone = TimeZone.currentSystemDefault()).toEpochMilliseconds()
}

private fun List<ElementFaceEntity>.toModel(): List<ElementFace> {
    return this.map {
        ElementFace(elementId = it.elementId, faceIndex = it.faceIndex)
    }
}

private fun List<ElementFace>.toEntity(): List<ElementFaceEntity> {
    return this.map {
        ElementFaceEntity(elementId = it.elementId, faceIndex = it.faceIndex)
    }
}

