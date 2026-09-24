package com.rostykstets.fembodydeformer.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.rostykstets.fembodydeformer.data.converters.Converters
import com.rostykstets.fembodydeformer.data.daos.SimulationRecordDao
import com.rostykstets.fembodydeformer.data.entities.SimulationRecordEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

@Database(
    entities = [SimulationRecordEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class FEMBodyDeformerDatabase: RoomDatabase() {

    abstract fun getSimulationRecordDao(): SimulationRecordDao
}

@Suppress("KotlinNoActualForExpect")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<FEMBodyDeformerDatabase> {
    override fun initialize(): FEMBodyDeformerDatabase
}

fun getFEMBodyDeformerDatabase(
    builder: RoomDatabase.Builder<FEMBodyDeformerDatabase>
): FEMBodyDeformerDatabase {
    return builder
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()
}