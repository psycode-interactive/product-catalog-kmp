package com.psycodeinteractive.productcatalog.domain.usecase

import com.psycodeinteractive.productcatalog.domain.fixture.product
import com.psycodeinteractive.productcatalog.domain.repository.ProductRepository
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetProductDetailsUseCaseTest {

    private val testProduct = product()

    private val productRepository = mock<ProductRepository> {
        everySuspend { getProduct(testProduct.id) } returns Result.success(testProduct)
    }

    private fun getProductDetailsUseCase() = GetProductDetailsUseCase(
        productRepository
    )

    @Test
    fun `should return product when product id exists`() = runTest {
        val sut = getProductDetailsUseCase()
        val actual = sut(testProduct.id)
        assertEquals(Result.success(testProduct), actual)
    }
}
