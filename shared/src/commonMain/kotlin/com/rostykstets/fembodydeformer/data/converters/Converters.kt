package com.rostykstets.fembodydeformer.data.converters

import androidx.room.TypeConverter
import com.rostykstets.fembodydeformer.data.entities.ElementFaceEntity
import kotlinx.serialization.json.Json

class Converters {

    @TypeConverter
    fun fromFacesList(faces: List<ElementFaceEntity>): String {
        return Json.encodeToString(faces)
    }

    @TypeConverter
    fun toFacesList(json: String): List<ElementFaceEntity> {

        if(json.isBlank()) return emptyList()

        return Json.decodeFromString(json)
    }
}