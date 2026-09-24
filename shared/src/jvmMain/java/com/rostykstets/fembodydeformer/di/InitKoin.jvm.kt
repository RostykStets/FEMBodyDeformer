package com.rostykstets.fembodydeformer.di

import com.rostykstets.fembodydeformer.data.database.getDatabaseBuilder
import com.rostykstets.fembodydeformer.data.database.getFEMBodyDeformerDatabase
import org.koin.dsl.module

actual val platformModule = module {
    val builder = getDatabaseBuilder()
    getFEMBodyDeformerDatabase(builder)
}