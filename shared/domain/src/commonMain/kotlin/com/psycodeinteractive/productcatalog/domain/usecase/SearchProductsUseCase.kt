package com.psycodeinteractive.productcatalog.domain.usecase

import androidx.paging.PagingData
import com.psycodeinteractive.productcatalog.domain.model.Product
import com.psycodeinteractive.productcatalog.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow

class SearchProductsUseCase(
    private val productRepository: ProductRepository,
) {
    suspend operator fun invoke(query: String): Flow<PagingData<Product>> {
        return productRepository.searchProducts(query)
    }
}
