package com.rostykstets.fembodydeformer.di.presentation

import com.rostykstets.fembodydeformer.ui.screens.records.viewmodels.RecordsViewModel
import com.rostykstets.fembodydeformer.ui.screens.scene.viewmodels.FEMViewModel
import com.rostykstets.fembodydeformer.ui.screens.splash.viewmodels.PreferencesViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel { FEMViewModel(get(), get(), get(), get()) }

    viewModel { RecordsViewModel(get(), get()) }

    viewModel { PreferencesViewModel(get()) }
}