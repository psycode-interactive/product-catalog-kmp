package com.psycodeinteractive.productcatalog.data.di.source.remote.service

import com.psycodeinteractive.productcatalog.data.di.source.remote.model.ProductApiModel
import com.psycodeinteractive.productcatalog.data.di.source.remote.model.ProductListResponseApiModel

interface ProductApiService {
    suspend fun getProducts(
        limit: Int? = null,
        skip: Int? = null,
    ): ProductListResponseApiModel

    suspend fun searchProducts(
        query: String,
        limit: Int? = null,
        skip: Int? = null,
    ): ProductListResponseApiModel

    suspend fun getProductDetails(
        id: Long,
    ): ProductApiModel
}
