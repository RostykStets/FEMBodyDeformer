package com.rostykstets.fembodydeformer.di

import com.rostykstets.fembodydeformer.ui.viewmodels.FEMViewModel
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel

val appModule = module {

    viewModel { FEMViewModel(get()) }
}