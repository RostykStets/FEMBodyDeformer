package com.rostykstets.femlab

import android.app.Application
import com.rostykstets.femlab.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class FEMLabApp: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@FEMLabApp)
            androidLogger()
        }
    }
}