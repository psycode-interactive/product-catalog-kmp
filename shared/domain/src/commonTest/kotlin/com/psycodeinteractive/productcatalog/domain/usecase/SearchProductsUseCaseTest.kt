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

class SearchProductsUseCaseTest {

    private val testProduct = product()
    private val pagingData = PagingData.from(listOf(testProduct))
    private val query = "Smartphone"

    private val productRepository = mock<ProductRepository> {
        everySuspend { searchProducts(query) } returns flowOf(pagingData)
    }

    private fun searchProductsUseCase() = SearchProductsUseCase(
        productRepository
    )

    @Test
    fun `should return paged products for a given query`() = runTest {
        val sut = searchProductsUseCase()
        sut(query).test {
            assertEquals(pagingData, awaitItem())
            awaitComplete()
        }
    }
}
