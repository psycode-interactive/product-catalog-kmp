package com.psycodeinteractive.productcatalog.presentation.model

import com.psycodeinteractive.productcatalog.domain.model.Product

data class ProductPresentationModel(
    val id: Long,
    val title: String,
    val description: String,
    val isFavorite: Boolean,
    val price: Double,
)

fun Product.toPresentation() = ProductPresentationModel(
    id = id,
    title = title,
    description = description,
    isFavorite = isFavorite,
    price = price
)

fun ProductPresentationModel.toDomain() = Product(
    id = id,
    title = title,
    description = description,
    isFavorite = isFavorite,
    price = price
)
