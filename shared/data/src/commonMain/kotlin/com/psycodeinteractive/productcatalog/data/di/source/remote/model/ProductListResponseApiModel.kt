package com.psycodeinteractive.productcatalog.data.di.source.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductListResponseApiModel(
    val products: List<ProductApiModel>,
    val total: Long,
    val skip: Long,
    val limit: Long,
)
