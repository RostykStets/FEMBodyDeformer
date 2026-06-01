package com.rostykstets.femlab.di

import com.rostykstets.femlab.domain.FEMBody
import com.rostykstets.femlab.ui.FEMViewModel
import org.koin.dsl.module
import org.koin.core.module.dsl.viewModel

val appModule = module {
    factory { FEMBody() }

    viewModel { FEMViewModel(get()) }
}