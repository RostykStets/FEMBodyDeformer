package com.rostykstets.fembodydeformer.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.rostykstets.fembodydeformer.data.database.FEMBodyDeformerDatabase
import com.rostykstets.fembodydeformer.data.database.getDatabaseBuilder
import com.rostykstets.fembodydeformer.data.database.getFEMBodyDeformerDatabase
import com.rostykstets.fembodydeformer.data.sharedPreferences.DATA_STORE_FILE_NAME
import com.rostykstets.fembodydeformer.data.sharedPreferences.createDataStore
import org.koin.dsl.module
import java.io.File

actual val platformModule = module {
    single<FEMBodyDeformerDatabase> {
        val builder = getDatabaseBuilder()
        getFEMBodyDeformerDatabase(builder)
    }

    single<DataStore<Preferences>> {
        createDataStore {
            val home = System.getProperty("user.home")
            val appDir = File(home, ".fem_body_deformer_app")
            if (!appDir.exists()) {
                appDir.mkdirs()
            }
            File(appDir, DATA_STORE_FILE_NAME).absolutePath
        }
    }
}