package com.psycodeinteractive.productcatalog.domain.di

import com.psycodeinteractive.productcatalog.domain.usecase.GetFavoriteProductsUseCase
import com.psycodeinteractive.productcatalog.domain.usecase.GetProductDetailsUseCase
import com.psycodeinteractive.productcatalog.domain.usecase.GetProductsUseCase
import com.psycodeinteractive.productcatalog.domain.usecase.SearchProductsUseCase
import com.psycodeinteractive.productcatalog.domain.usecase.ToggleFavoriteProductUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::GetProductsUseCase)
    factoryOf(::GetProductDetailsUseCase)
    factoryOf(::GetFavoriteProductsUseCase)
    factoryOf(::ToggleFavoriteProductUseCase)
    factoryOf(::SearchProductsUseCase)
}
