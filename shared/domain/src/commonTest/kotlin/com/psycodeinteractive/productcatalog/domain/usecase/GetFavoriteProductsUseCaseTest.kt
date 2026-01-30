package com.psycodeinteractive.productcatalog.domain.usecase

import app.cash.turbine.test
import com.psycodeinteractive.productcatalog.domain.fixture.product
import com.psycodeinteractive.productcatalog.domain.repository.ProductRepository
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.mock
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetFavoriteProductsUseCaseTest {

    private val testProduct = product()
    private val favoriteProducts = listOf(testProduct)

    private val productRepository = mock<ProductRepository> {
        every { getFavoriteProducts() } returns flowOf(favoriteProducts)
    }

    private fun getFavoriteProductsUseCase() = GetFavoriteProductsUseCase(
        productRepository
    )

    @Test
    fun `should return favorite products`() = runTest {
        val sut = getFavoriteProductsUseCase()
        sut().test {
            assertEquals(favoriteProducts, awaitItem())
            awaitComplete()
        }
    }
}
