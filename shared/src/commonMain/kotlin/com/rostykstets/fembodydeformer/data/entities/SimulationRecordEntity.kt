package com.rostykstets.fembodydeformer.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SimulationRecords")
data class SimulationRecordEntity(
    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "title")
    val title: String = "",
    @ColumnInfo(name = "date_added")
    val dateAdded: Long,
    @ColumnInfo(name = "date_modified")
    val dateModified: Long,
    @ColumnInfo(name = "size_x")
    val sizeX: Float = 0f,
    @ColumnInfo(name = "size_y")
    val sizeY: Float = 0f,
    @ColumnInfo(name = "size_z")
    val sizeZ: Float = 0f,
    @ColumnInfo(name = "split_x")
    val splitX: Int = 0,
    @ColumnInfo(name = "split_y")
    val splitY: Int = 0,
    @ColumnInfo(name = "split_z")
    val splitZ: Int = 0,
    @ColumnInfo(name = "young_module")
    val youngModule: Double = 0.0,
    @ColumnInfo(name = "poisson_ratio")
    val poissonRatio: Double = 0.0,
    @ColumnInfo(name = "pressure")
    val pressure: Double = 0.0,
    @ColumnInfo(name = "fixed_faces")
    val fixedFaces: List<ElementFaceEntity>,
    @ColumnInfo(name = "loaded_faces")
    val loadedFaces: List<ElementFaceEntity>,
    @ColumnInfo(name = "is_body_deformed")
    val isBodyDeformed: Boolean = false
)
