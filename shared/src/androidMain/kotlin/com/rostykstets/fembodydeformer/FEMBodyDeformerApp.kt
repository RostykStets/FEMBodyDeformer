package com.rostykstets.fembodydeformer

import android.app.Application
import com.rostykstets.fembodydeformer.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class FEMBodyDeformerApp: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@FEMBodyDeformerApp)
            androidLogger()
        }
    }
}