package com.rostykstets.fembodydeformer.di.presentation

import com.rostykstets.fembodydeformer.ui.screens.records.viewmodels.RecordsViewModel
import com.rostykstets.fembodydeformer.ui.viewmodels.FEMViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { FEMViewModel(get()) }

    viewModel { RecordsViewModel(get()) }
}