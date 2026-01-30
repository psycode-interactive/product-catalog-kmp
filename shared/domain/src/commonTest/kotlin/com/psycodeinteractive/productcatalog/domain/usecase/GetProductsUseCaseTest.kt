package com.psycodeinteractive.productcatalog.domain.usecase

import androidx.paging.PagingData
import app.cash.turbine.test
import com.psycodeinteractive.productcatalog.domain.fixture.product
import com.psycodeinteractive.productcatalog.domain.repository.ProductRepository
import dev.mokkery.answering.returns
import dev.mokkery.everySuspend
import dev.mokkery.mock
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetProductsUseCaseTest {

    private val testProduct = product()
    private val pagingData = PagingData.from(listOf(testProduct))

    private val productRepository = mock<ProductRepository> {
        everySuspend { getProducts() } returns flowOf(pagingData)
    }

    private fun getProductsUseCase() = GetProductsUseCase(
        productRepository
    )

    @Test
    fun `should return paged products`() = runTest {
        val sut = getProductsUseCase()
        sut().test {
            assertEquals(pagingData, awaitItem())
            awaitComplete()
        }
    }
}
