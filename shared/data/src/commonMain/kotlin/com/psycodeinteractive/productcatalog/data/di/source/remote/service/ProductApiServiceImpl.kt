package com.psycodeinteractive.productcatalog.data.di.source.remote.service

import com.psycodeinteractive.productcatalog.data.di.source.remote.model.ProductApiModel
import com.psycodeinteractive.productcatalog.data.di.source.remote.model.ProductListResponseApiModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class ProductApiServiceImpl(
    private val client: HttpClient,
) : ProductApiService {
    override suspend fun getProducts(
        limit: Int?,
        skip: Int?,
    ): ProductListResponseApiModel = client.get("/products") {
        parameter("limit", limit)
        parameter("skip", skip)
    }.body()

    override suspend fun searchProducts(
        query: String,
        limit: Int?,
        skip: Int?,
    ): ProductListResponseApiModel = client.get("/products/search") {
        parameter("q", query)
        parameter("limit", limit)
        parameter("skip", skip)
    }.body()

    override suspend fun getProductDetails(
        id: Long
    ): ProductApiModel = client.get("/products/$id").body()
}
