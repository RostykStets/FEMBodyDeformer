package com.rostykstets.fembodydeformer.di

import com.rostykstets.fembodydeformer.data.database.FEMBodyDeformerDatabase
import com.rostykstets.fembodydeformer.data.database.getDatabaseBuilder
import com.rostykstets.fembodydeformer.data.database.getFEMBodyDeformerDatabase
import org.koin.dsl.module

actual val platformModule = module {

    single<FEMBodyDeformerDatabase> {
        val builder = getDatabaseBuilder(context = get())
        getFEMBodyDeformerDatabase(builder)
    }
}