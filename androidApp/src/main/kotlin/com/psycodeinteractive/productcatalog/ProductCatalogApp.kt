package com.psycodeinteractive.productcatalog

import android.app.Application
import com.psycodeinteractive.productcatalog.app.di.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class ProductCatalogApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@ProductCatalogApp)
        }
    }
}
