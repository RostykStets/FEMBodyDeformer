package com.rostykstets.fembodydeformer.data.database

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

fun getDatabaseBuilder(): RoomDatabase.Builder<FEMBodyDeformerDatabase> {
    val dbFile = File(System.getProperty("java.io.tmpdir"), "fem_body_deformer.db")
    return Room.databaseBuilder<FEMBodyDeformerDatabase>(
        name = dbFile.absolutePath,
    )
}