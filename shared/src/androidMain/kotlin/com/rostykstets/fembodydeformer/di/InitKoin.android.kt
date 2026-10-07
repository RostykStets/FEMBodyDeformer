package com.rostykstets.fembodydeformer.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.rostykstets.fembodydeformer.data.database.FEMBodyDeformerDatabase
import com.rostykstets.fembodydeformer.data.database.getDatabaseBuilder
import com.rostykstets.fembodydeformer.data.database.getFEMBodyDeformerDatabase
import com.rostykstets.fembodydeformer.data.sharedPreferences.DATA_STORE_FILE_NAME
import com.rostykstets.fembodydeformer.data.sharedPreferences.createDataStore
import org.koin.dsl.module

actual val platformModule = module {

    single<FEMBodyDeformerDatabase> {
        val builder = getDatabaseBuilder(context = get())
        getFEMBodyDeformerDatabase(builder)
    }

    single<DataStore<Preferences>> {
        createDataStore {
            get<Context>().filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath
        }
    }
}