package com.psycodeinteractive.productcatalog.domain.usecase

import com.psycodeinteractive.productcatalog.domain.fixture.product
import com.psycodeinteractive.productcatalog.domain.repository.ProductRepository
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class ToggleFavoriteProductUseCaseTest {

    private val testProduct = product()

    private val productRepository = mock<ProductRepository> {
        everySuspend { saveFavorite(testProduct) } returns Result.success(Unit)
        everySuspend { deleteFavorite(testProduct.id) } returns Result.success(Unit)
    }

    private fun toggleFavoriteProductUseCase() = ToggleFavoriteProductUseCase(
        productRepository
    )

    @Test
    fun `should save favorite when product is not favorite`() = runTest {
        val sut = toggleFavoriteProductUseCase()
        val productToFavorite = testProduct.copy(isFavorite = false)
        val actual = sut(productToFavorite)
        assertEquals(Result.success(Unit), actual)
        verifySuspend { productRepository.saveFavorite(productToFavorite) }
    }

    @Test
    fun `should delete favorite when product is favorite`() = runTest {
        val sut = toggleFavoriteProductUseCase()
        val productToUnfavorite = testProduct.copy(isFavorite = true)
        val actual = sut(productToUnfavorite)
        assertEquals(Result.success(Unit), actual)
        verifySuspend { productRepository.deleteFavorite(productToUnfavorite.id) }
    }
}
