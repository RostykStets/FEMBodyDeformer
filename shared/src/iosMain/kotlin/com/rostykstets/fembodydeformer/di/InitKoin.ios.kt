package com.rostykstets.fembodydeformer.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.rostykstets.fembodydeformer.data.database.FEMBodyDeformerDatabase
import com.rostykstets.fembodydeformer.data.database.getDatabaseBuilder
import com.rostykstets.fembodydeformer.data.database.getFEMBodyDeformerDatabase
import com.rostykstets.fembodydeformer.data.sharedPreferences.DATA_STORE_FILE_NAME
import com.rostykstets.fembodydeformer.data.sharedPreferences.createDataStore
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual val platformModule = module {
    single<FEMBodyDeformerDatabase> {
        val builder = getDatabaseBuilder()
        getFEMBodyDeformerDatabase(builder)
    }

    single<DataStore<Preferences>> {
        createDataStore {
            val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
                directory = NSDocumentDirectory,
                inDomain = NSUserDomainMask,
                appropriateForURL = null,
                create = false,
                error = null
            )
            requireNotNull(documentDirectory?.path) + "/$DATA_STORE_FILE_NAME"
        }
    }
}