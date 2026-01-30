package com.psycodeinteractive.productcatalog.domain.repository

import androidx.paging.PagingData
import com.psycodeinteractive.productcatalog.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun getProducts(): Flow<PagingData<Product>>
    suspend fun searchProducts(query: String): Flow<PagingData<Product>>
    suspend fun getProduct(id: Long): Result<Product>
    fun getFavoriteProducts(): Flow<List<Product>>
    suspend fun saveFavorite(product: Product): Result<Unit>
    suspend fun deleteFavorite(productId: Long): Result<Unit>
}
