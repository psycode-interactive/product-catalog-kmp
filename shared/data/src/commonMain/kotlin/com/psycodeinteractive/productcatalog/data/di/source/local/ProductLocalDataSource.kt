package com.psycodeinteractive.productcatalog.data.di.source.local

import com.psycodeinteractive.productcatalog.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface ProductLocalDataSource {
    fun getFavorites(): Flow<List<Product>>
    suspend fun getFavoriteIds(): Set<Long>
    suspend fun saveFavorite(product: Product)
    suspend fun deleteFavorite(productId: Long)
}
