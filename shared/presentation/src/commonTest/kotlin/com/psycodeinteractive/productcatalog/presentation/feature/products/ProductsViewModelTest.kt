@file:Suppress("UnusedFlow")

package com.psycodeinteractive.productcatalog.presentation.feature.products

import androidx.paging.PagingData
import app.cash.turbine.test
import com.psycodeinteractive.productcatalog.domain.repository.ProductRepository
import com.psycodeinteractive.productcatalog.domain.usecase.GetFavoriteProductsUseCase
import com.psycodeinteractive.productcatalog.domain.usecase.GetProductsUseCase
import com.psycodeinteractive.productcatalog.domain.usecase.SearchProductsUseCase
import com.psycodeinteractive.productcatalog.domain.usecase.ToggleFavoriteProductUseCase
import com.psycodeinteractive.productcatalog.presentation.feature.products.ProductsSideEffect.Navigation
import com.psycodeinteractive.productcatalog.presentation.feature.products.ProductsViewEvent.GoToDetails
import com.psycodeinteractive.productcatalog.presentation.feature.products.ProductsViewEvent.GoToFavorites
import com.psycodeinteractive.productcatalog.presentation.feature.products.ProductsViewEvent.QueryChanged
import com.psycodeinteractive.productcatalog.presentation.feature.products.ProductsViewEvent.ToggleFavorite
import com.psycodeinteractive.productcatalog.presentation.fixture.product
import com.psycodeinteractive.productcatalog.presentation.fixture.productPresentationModel
import com.psycodeinteractive.productcatalog.presentation.model.toDomain
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verify.VerifyMode
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ProductsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val testProduct = product()

    private val testPresentationProduct = productPresentationModel()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private val productRepository = mock<ProductRepository> {
        everySuspend { getProducts() } returns flowOf(PagingData.from(listOf(testProduct)))
        everySuspend { searchProducts(any()) } returns flowOf(PagingData.from(listOf(testProduct)))
        every { getFavoriteProducts() } returns flowOf(listOf(testProduct))
        everySuspend { saveFavorite(any()) } returns Result.success(Unit)
        everySuspend { deleteFavorite(any()) } returns Result.success(Unit)
    }

    private val getProductsUseCase = GetProductsUseCase(productRepository)
    private val searchProductsUseCase = SearchProductsUseCase(productRepository)
    private val toggleFavoriteProductUseCase = ToggleFavoriteProductUseCase(productRepository)
    private val getFavoriteProductsUseCase = GetFavoriteProductsUseCase(productRepository)

    private fun productsViewModel() = ProductsViewModel(
        getProductsUseCase,
        searchProductsUseCase,
        toggleFavoriteProductUseCase,
        getFavoriteProductsUseCase
    )

    @Test
    fun `should navigate to favorites when favorites button is clicked`() = runTest {
        val sut = productsViewModel()

        advanceUntilIdle()

        sut.sideEffects.test {
            sut.processEvent(GoToFavorites)
            assertEquals(Navigation.NavigateToFavorites, awaitItem())
        }
    }

    @Test
    fun `should navigate to product details when product is clicked`() = runTest {
        val sut = productsViewModel()

        advanceUntilIdle()

        sut.sideEffects.test {
            sut.processEvent(GoToDetails(testPresentationProduct))
            assertEquals(Navigation.NavigateToProductDetails(testPresentationProduct.id), awaitItem())
        }
    }

    @Test
    fun `should call toggle favorite use case when favorite is clicked`() = runTest {
        val sut = productsViewModel()

        advanceUntilIdle()

        sut.processEvent(ToggleFavorite(testPresentationProduct))
        advanceUntilIdle()

        verifySuspend { toggleFavoriteProductUseCase(testPresentationProduct.toDomain()) }
    }

    @Test
    fun `should not change state type when favorite is toggled`() = runTest {
        val sut = productsViewModel()

        sut.viewState.test {
            // Skip initial states
            skipItems(2) // Loading, Ready

            sut.processEvent(ToggleFavorite(testPresentationProduct))

            // The state is returned as-is from onToggleFavorite, so no new emission
            expectNoEvents()
        }
    }

    @Test
    fun `should transition to Search state with loading when query is entered`() = runTest {
        val sut = productsViewModel()
        val query = "Smartphone"

        sut.viewState.test {
            // Skip initial states
            assertEquals(ProductsViewState.Loading, awaitItem())
            val readyState = awaitItem()
            assertTrue(readyState is ProductsViewState.Ready)

            // Trigger search
            sut.processEvent(QueryChanged(query))

            // Should immediately transition to Search state with loading
            val searchingState = awaitItem()
            assertTrue(searchingState is ProductsViewState.Search)
            assertEquals(query, searchingState.query)
            assertTrue(searchingState.isLoading)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should debounce search and call search use case after delay`() = runTest {
        val sut = productsViewModel()
        val query = "Smartphone"

        sut.viewState.test {
            // Skip to Ready state
            skipItems(2) // Loading, Ready

            // Trigger search
            sut.processEvent(QueryChanged(query))
            skipItems(1) // Search with loading

            // Advance time by debounce duration
            advanceTimeBy(SEARCH_DEBOUNCE_MS)

            // Should complete search
            val searchResultState = awaitItem()
            assertTrue(searchResultState is ProductsViewState.Search)
            assertEquals(query, searchResultState.query)
            assertFalse(searchResultState.isLoading)

            verifySuspend { searchProductsUseCase(query) }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should cancel previous search when new query is entered`() = runTest {
        val sut = productsViewModel()

        sut.viewState.test {
            skipItems(2) // Loading, Ready

            // Enter first query
            sut.processEvent(QueryChanged("First"))
            skipItems(1) // Search with loading

            // Enter second query before debounce
            advanceTimeBy(200)
            sut.processEvent(QueryChanged("Second"))
            skipItems(1) // Search with loading for "Second"

            // Advance past debounce
            advanceTimeBy(SEARCH_DEBOUNCE_MS)

            // Should only search for "Second"
            val searchResultState = awaitItem()
            assertTrue(searchResultState is ProductsViewState.Search)
            assertEquals("Second", searchResultState.query)

            // Should NOT have called search for "First"
            verifySuspend(mode = VerifyMode.not) { searchProductsUseCase("First") }
            verifySuspend { searchProductsUseCase("Second") }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should return to products list when search query is cleared`() = runTest {
        val sut = productsViewModel()

        sut.viewState.test {
            skipItems(2) // Loading, Ready

            // Search for something
            sut.processEvent(QueryChanged("Smartphone"))
            skipItems(1) // Search with loading
            advanceTimeBy(SEARCH_DEBOUNCE_MS)
            skipItems(1) // Search completed

            // Clear search
            sut.processEvent(QueryChanged(""))
            skipItems(1) // Search with empty query
            advanceTimeBy(SEARCH_DEBOUNCE_MS)

            // Should return to Ready state with all products
            val state = awaitItem()
            assertTrue(state is ProductsViewState.Ready)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should initialize with Loading state`() = runTest {
        val sut = productsViewModel()

        sut.viewState.test {
            assertEquals(ProductsViewState.Loading, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `should load products on initialization`() = runTest {
        val sut = productsViewModel()

        sut.viewState.test {
            assertEquals(ProductsViewState.Loading, awaitItem())

            val readyState = awaitItem()
            assertTrue(readyState is ProductsViewState.Ready)

            verifySuspend { productRepository.getProducts() }

            cancelAndIgnoreRemainingEvents()
        }
    }
}
