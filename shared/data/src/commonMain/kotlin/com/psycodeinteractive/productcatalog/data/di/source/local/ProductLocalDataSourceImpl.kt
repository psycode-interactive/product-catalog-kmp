package com.psycodeinteractive.productcatalog.data.di.source.local

import com.psycodeinteractive.productcatalog.domain.model.Product
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProductLocalDataSourceImpl(
    private val dao: FavoriteProductDao,
) : ProductLocalDataSource {

    override fun getFavorites(): Flow<List<Product>> =
        dao.getFavorites().map { products ->
            products.map { it.toDomain() }
        }

    override suspend fun getFavoriteIds(): Set<Long> =
        dao.getFavoriteIds().toSet()

    override suspend fun saveFavorite(product: Product) {
        dao.insert(product.toDatabase())
    }

    override suspend fun deleteFavorite(productId: Long) {
        dao.delete(productId)
    }
}
