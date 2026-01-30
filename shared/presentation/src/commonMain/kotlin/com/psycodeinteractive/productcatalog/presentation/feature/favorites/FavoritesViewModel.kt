package com.psycodeinteractive.productcatalog.presentation.feature.favorites

import androidx.lifecycle.viewModelScope
import com.psycodeinteractive.productcatalog.domain.model.Product
import com.psycodeinteractive.productcatalog.domain.usecase.GetFavoriteProductsUseCase
import com.psycodeinteractive.productcatalog.domain.usecase.ToggleFavoriteProductUseCase
import com.psycodeinteractive.productcatalog.presentation.BaseViewModel
import com.psycodeinteractive.productcatalog.presentation.ViewEvent
import com.psycodeinteractive.productcatalog.presentation.ViewSideEffect
import com.psycodeinteractive.productcatalog.presentation.ViewState
import com.psycodeinteractive.productcatalog.presentation.feature.favorites.FavoritesSideEffect.Navigation
import com.psycodeinteractive.productcatalog.presentation.feature.favorites.FavoritesViewEvent.Back
import com.psycodeinteractive.productcatalog.presentation.feature.favorites.FavoritesViewEvent.FavoriteToggled
import com.psycodeinteractive.productcatalog.presentation.feature.favorites.FavoritesViewEvent.GoToDetails
import com.psycodeinteractive.productcatalog.presentation.feature.favorites.FavoritesViewState.Failure
import com.psycodeinteractive.productcatalog.presentation.feature.favorites.FavoritesViewState.Loading
import com.psycodeinteractive.productcatalog.presentation.feature.favorites.FavoritesViewState.Ready
import com.psycodeinteractive.productcatalog.presentation.feature.favorites.InternalFavoritesViewEvent.FailedToLoad
import com.psycodeinteractive.productcatalog.presentation.feature.favorites.InternalFavoritesViewEvent.ProductsLoaded
import com.psycodeinteractive.productcatalog.presentation.model.ProductPresentationModel
import com.psycodeinteractive.productcatalog.presentation.model.toDomain
import com.psycodeinteractive.productcatalog.presentation.model.toPresentation
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val getFavoriteProductsUseCase: GetFavoriteProductsUseCase,
    private val toggleFavoriteProductUseCase: ToggleFavoriteProductUseCase,
) : BaseViewModel<FavoritesViewState, FavoritesViewEvent, FavoritesSideEffect>() {
    override val initialViewState = Loading

    init {
        observeFavoriteProducts()
    }

    override fun reduceState(
        currentState: FavoritesViewState,
        event: FavoritesViewEvent,
    ) = when (event) {
        is ProductsLoaded -> Ready(event.products)
        is FavoriteToggled -> onToggleFavorite(currentState, event)
        is GoToDetails -> onGoToDetails(currentState, event)
        FailedToLoad -> Failure
        Back -> {
            postSideEffect(Navigation.NavigateUp)
            currentState
        }
    }

    private fun observeFavoriteProducts() {
        viewModelScope.launch {
            getFavoriteProductsUseCase()
                .catch { processEvent(FailedToLoad) }
                .collect { products ->
                    val products = products.map(Product::toPresentation)
                    processEvent(ProductsLoaded(products))
                }
        }
    }

    private fun onToggleFavorite(
        currentState: FavoritesViewState,
        event: FavoriteToggled,
    ): FavoritesViewState {
        viewModelScope.launch {
            val result = toggleFavoriteProductUseCase(event.product.toDomain())
            @Suppress("ControlFlowWithEmptyBody")
            if (result.isFailure) {
                // We can handle the potential failure here
            }
        }
        return currentState
    }

    private fun onGoToDetails(
        currentState: FavoritesViewState,
        event: GoToDetails,
    ) : FavoritesViewState {
        postSideEffect(Navigation.NavigateToProductDetails(event.product.id))
        return currentState
    }
}

sealed interface FavoritesViewState : ViewState {
    data object Loading : FavoritesViewState
    data class Ready(
        val products: List<ProductPresentationModel>,
    ) : FavoritesViewState

    data object Failure : FavoritesViewState
}

sealed interface FavoritesViewEvent : ViewEvent {
    data class FavoriteToggled(
        val product: ProductPresentationModel,
    ) : FavoritesViewEvent

    data class GoToDetails(
        val product: ProductPresentationModel,
    ) : FavoritesViewEvent

    data object Back : FavoritesViewEvent
}

private sealed interface InternalFavoritesViewEvent : FavoritesViewEvent {
    data class ProductsLoaded(
        val products: List<ProductPresentationModel>,
    ) : InternalFavoritesViewEvent

    data object FailedToLoad : InternalFavoritesViewEvent
}

sealed interface FavoritesSideEffect : ViewSideEffect {
    sealed interface Navigation : FavoritesSideEffect {
        data object NavigateUp : Navigation
        data class NavigateToProductDetails(
            val productId: Long,
        ) : Navigation
    }
}
