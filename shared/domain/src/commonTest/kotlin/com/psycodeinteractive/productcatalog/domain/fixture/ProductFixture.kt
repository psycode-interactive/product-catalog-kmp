package com.psycodeinteractive.productcatalog.domain.fixture

import com.psycodeinteractive.productcatalog.domain.model.Product

fun product(
    id: Long = 1,
    title: String = "Smartphone",
    description: String = "Latest model with advanced features",
    isFavorite: Boolean = false,
    price: Double = 999.99
) = Product(
    id = id,
    title = title,
    description = description,
    price = price,
    isFavorite = isFavorite
)
