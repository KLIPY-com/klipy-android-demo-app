package com.klipy.demoapp

import android.app.Application
import com.klipy.demoapp.data.di.dataModule
import com.klipy.demoapp.data.di.networkModule
import com.klipy.demoapp.presentation.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class KlipyDemoApp: Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@KlipyDemoApp)
            modules(appModule, networkModule, dataModule)
        }
    }
}