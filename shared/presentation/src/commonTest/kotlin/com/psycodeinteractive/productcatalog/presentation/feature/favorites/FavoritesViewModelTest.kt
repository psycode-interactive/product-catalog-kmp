@file:Suppress("UnusedFlow")

package com.psycodeinteractive.productcatalog.presentation.feature.favorites

import app.cash.turbine.test
import com.psycodeinteractive.productcatalog.domain.repository.ProductRepository
import com.psycodeinteractive.productcatalog.domain.usecase.GetFavoriteProductsUseCase
import com.psycodeinteractive.productcatalog.domain.usecase.ToggleFavoriteProductUseCase
import com.psycodeinteractive.productcatalog.presentation.feature.favorites.FavoritesSideEffect.Navigation
import com.psycodeinteractive.productcatalog.presentation.feature.favorites.FavoritesViewEvent.Back
import com.psycodeinteractive.productcatalog.presentation.feature.favorites.FavoritesViewEvent.FavoriteToggled
import com.psycodeinteractive.productcatalog.presentation.feature.favorites.FavoritesViewEvent.GoToDetails
import com.psycodeinteractive.productcatalog.presentation.fixture.product
import com.psycodeinteractive.productcatalog.presentation.fixture.productPresentationModel
import com.psycodeinteractive.productcatalog.presentation.model.toDomain
import dev.mokkery.answering.returns
import dev.mokkery.every
import dev.mokkery.everySuspend
import dev.mokkery.matcher.any
import dev.mokkery.mock
import dev.mokkery.verifySuspend
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FavoritesViewModelTest {

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
        every { getFavoriteProducts() } returns flowOf(listOf(testProduct))
        everySuspend { saveFavorite(any()) } returns Result.success(Unit)
        everySuspend { deleteFavorite(any()) } returns Result.success(Unit)
    }

    private val getFavoriteProductsUseCase = GetFavoriteProductsUseCase(productRepository)
    private val toggleFavoriteProductUseCase = ToggleFavoriteProductUseCase(productRepository)

    private fun favoritesViewModel() = FavoritesViewModel(
        getFavoriteProductsUseCase,
        toggleFavoriteProductUseCase
    )

    @Test
    fun `should navigate up when Back event is processed`() = runTest {
        val sut = favoritesViewModel()

        advanceUntilIdle()

        sut.sideEffects.test {
            sut.processEvent(Back)
            assertEquals(Navigation.NavigateUp, awaitItem())
        }
    }

    @Test
    fun `should navigate to product details when GoToDetails event is processed`() = runTest {
        val sut = favoritesViewModel()

        advanceUntilIdle()

        sut.sideEffects.test {
            sut.processEvent(GoToDetails(testPresentationProduct))
            assertEquals(Navigation.NavigateToProductDetails(testPresentationProduct.id), awaitItem())
        }
    }

    @Test
    fun `should call toggle favorite use case when favorite is toggled`() = runTest {
        val sut = favoritesViewModel()

        advanceUntilIdle()

        sut.processEvent(FavoriteToggled(testPresentationProduct))
        advanceUntilIdle()

        verifySuspend { toggleFavoriteProductUseCase(testPresentationProduct.toDomain()) }
    }
}
