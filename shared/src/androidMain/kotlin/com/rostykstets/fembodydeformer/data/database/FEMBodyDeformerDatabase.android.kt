package com.rostykstets.fembodydeformer.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<FEMBodyDeformerDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath("fem_body_deformer.db")
    return Room.databaseBuilder<FEMBodyDeformerDatabase>(
        context = appContext,
        name = dbFile.absolutePath
    )
}