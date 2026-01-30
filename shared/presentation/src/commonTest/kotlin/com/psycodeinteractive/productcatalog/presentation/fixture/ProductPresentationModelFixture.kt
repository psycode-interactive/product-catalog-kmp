package com.psycodeinteractive.productcatalog.presentation.fixture

import com.psycodeinteractive.productcatalog.presentation.model.ProductPresentationModel

fun productPresentationModel(
    id: Long = 1,
    title: String = "Smartphone",
    description: String = "Latest model with advanced features",
    isFavorite: Boolean = false,
    price: Double = 999.99
) = ProductPresentationModel(
    id = id,
    title = title,
    description = description,
    isFavorite = isFavorite,
    price = price
)
