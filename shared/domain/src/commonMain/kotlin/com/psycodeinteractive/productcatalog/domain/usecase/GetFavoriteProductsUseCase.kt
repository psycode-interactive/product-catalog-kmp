package com.psycodeinteractive.productcatalog.domain.usecase

import com.psycodeinteractive.productcatalog.domain.model.Product
import com.psycodeinteractive.productcatalog.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow

class GetFavoriteProductsUseCase(
    private val productRepository: ProductRepository,
) {
    operator fun invoke(): Flow<List<Product>> {
        return productRepository.getFavoriteProducts()
    }
}
