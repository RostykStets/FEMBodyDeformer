package com.rostykstets.fembodydeformer.di

import com.rostykstets.fembodydeformer.di.data.daoModule
import com.rostykstets.fembodydeformer.di.domain.domainModule
import com.rostykstets.fembodydeformer.di.presentation.viewModelModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

expect val platformModule: Module

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(daoModule, domainModule, viewModelModule, platformModule)
    }
}