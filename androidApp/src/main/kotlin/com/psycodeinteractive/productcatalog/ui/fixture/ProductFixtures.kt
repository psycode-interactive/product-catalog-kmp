package com.psycodeinteractive.productcatalog.ui.fixture

import com.psycodeinteractive.productcatalog.presentation.model.ProductPresentationModel

val product1PresentationFixture = ProductPresentationModel(
    id = 1,
    title = "Smartphone",
    description = "Latest model with advanced features",
    isFavorite = false,
    price = 999.99
)
val product2PresentationFixture = ProductPresentationModel(
    id = 2,
    title = "Laptop",
    description = "High-performance for professionals",
    isFavorite = true,
    price = 1499.99
)
val product3PresentationFixture = ProductPresentationModel(
    id = 3,
    title = "Headphones",
    description = "Noise-cancelling with superior sound quality",
    isFavorite = false,
    price = 199.99
)

val productsPresentationFixtures = listOf(
    product1PresentationFixture,
    product2PresentationFixture,
    product3PresentationFixture,
)
