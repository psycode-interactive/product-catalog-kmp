package com.psycodeinteractive.productcatalog.domain.usecase

import com.psycodeinteractive.productcatalog.domain.model.Product
import com.psycodeinteractive.productcatalog.domain.repository.ProductRepository

class GetProductDetailsUseCase(
    private val productRepository: ProductRepository,
) {
    suspend operator fun invoke(id: Long): Result<Product> {
        return productRepository.getProduct(id)
    }
}
