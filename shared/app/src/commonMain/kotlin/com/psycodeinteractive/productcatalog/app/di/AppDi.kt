package com.psycodeinteractive.productcatalog.app.di

import com.psycodeinteractive.productcatalog.data.di.dataModule
import com.psycodeinteractive.productcatalog.domain.di.domainModule
import com.psycodeinteractive.productcatalog.presentation.di.presentationModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(appDeclaration: KoinAppDeclaration = {}) =
    startKoin {
        appDeclaration()
        modules(domainModule, dataModule, presentationModule)
    }
