package com.psycodeinteractive.productcatalog.domain.usecase

import com.psycodeinteractive.productcatalog.domain.model.Product
import com.psycodeinteractive.productcatalog.domain.repository.ProductRepository

class ToggleFavoriteProductUseCase(
    private val productRepository: ProductRepository,
) {
    suspend operator fun invoke(product: Product): Result<Unit> {
        return if (product.isFavorite) {
            productRepository.deleteFavorite(product.id)
        } else {
            productRepository.saveFavorite(product)
        }
    }
}
