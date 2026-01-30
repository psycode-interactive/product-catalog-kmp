package com.psycodeinteractive.productcatalog.presentation.di

import com.psycodeinteractive.productcatalog.presentation.feature.details.DetailsViewModel
import com.psycodeinteractive.productcatalog.presentation.feature.favorites.FavoritesViewModel
import com.psycodeinteractive.productcatalog.presentation.feature.products.ProductsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    viewModelOf(::ProductsViewModel)
    viewModelOf(::DetailsViewModel)
    viewModelOf(::FavoritesViewModel)
}
