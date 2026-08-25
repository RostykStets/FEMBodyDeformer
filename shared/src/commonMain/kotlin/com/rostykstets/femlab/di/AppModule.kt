package com.rostykstets.femlab.di

import com.rostykstets.femlab.domain.models.FEMBody
import com.rostykstets.femlab.ui.viewmodels.FEMViewModel
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel

val appModule = module {
    factory { FEMBody() }

    viewModel { FEMViewModel(get()) }
}