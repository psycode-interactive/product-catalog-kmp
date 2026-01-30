package com.psycodeinteractive.productcatalog.presentation.feature.details

import androidx.lifecycle.viewModelScope
import com.psycodeinteractive.productcatalog.domain.usecase.GetFavoriteProductsUseCase
import com.psycodeinteractive.productcatalog.domain.usecase.GetProductDetailsUseCase
import com.psycodeinteractive.productcatalog.domain.usecase.ToggleFavoriteProductUseCase
import com.psycodeinteractive.productcatalog.presentation.BaseViewModel
import com.psycodeinteractive.productcatalog.presentation.ViewEvent
import com.psycodeinteractive.productcatalog.presentation.ViewSideEffect
import com.psycodeinteractive.productcatalog.presentation.ViewState
import com.psycodeinteractive.productcatalog.presentation.feature.details.DetailsSideEffect.Navigation
import com.psycodeinteractive.productcatalog.presentation.feature.details.DetailsViewEvent.Back
import com.psycodeinteractive.productcatalog.presentation.feature.details.DetailsViewEvent.ToggleFavorite
import com.psycodeinteractive.productcatalog.presentation.feature.details.DetailsViewState.Failure
import com.psycodeinteractive.productcatalog.presentation.feature.details.DetailsViewState.Loading
import com.psycodeinteractive.productcatalog.presentation.feature.details.DetailsViewState.Ready
import com.psycodeinteractive.productcatalog.presentation.feature.details.InternalDetailsViewEvent.FailedToLoad
import com.psycodeinteractive.productcatalog.presentation.feature.details.InternalDetailsViewEvent.ProductLoaded
import com.psycodeinteractive.productcatalog.presentation.model.ProductPresentationModel
import com.psycodeinteractive.productcatalog.presentation.model.toDomain
import com.psycodeinteractive.productcatalog.presentation.model.toPresentation
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class DetailsViewModel(
    private val productId: Long,
    private val getProductDetailsUseCase: GetProductDetailsUseCase,
    private val toggleFavoriteProductUseCase: ToggleFavoriteProductUseCase,
    private val getFavoriteProductsUseCase: GetFavoriteProductsUseCase,
) : BaseViewModel<DetailsViewState, DetailsViewEvent, DetailsSideEffect>() {
    override val initialViewState: DetailsViewState = Loading

    init {
        observeFavoriteProducts()
    }

    override fun reduceState(
        currentState: DetailsViewState,
        event: DetailsViewEvent,
    ) = when (event) {
        is ProductLoaded -> Ready(event.product)
        is ToggleFavorite -> onToggleFavorite(currentState, event)
        FailedToLoad -> Failure
        Back -> {
            postSideEffect(Navigation.NavigateUp)
            currentState
        }
    }

    private fun getProductDetails() {
        viewModelScope.launch {
            val result = getProductDetailsUseCase(productId)
            result.fold(
                onSuccess = { product ->
                    processEvent(ProductLoaded(product.toPresentation()))
                },
                onFailure = {
                    processEvent(FailedToLoad)
                }
            )
        }
    }

    private fun observeFavoriteProducts() {
        viewModelScope.launch {
            getFavoriteProductsUseCase()
                .catch { processEvent(FailedToLoad) }
                .collect { getProductDetails() }
        }
    }

    private fun onToggleFavorite(
        currentState: DetailsViewState,
        event: ToggleFavorite,
    ): DetailsViewState {
        viewModelScope.launch {
            toggleFavoriteProductUseCase(event.product.toDomain())
        }
        return currentState
    }
}

sealed interface DetailsViewState : ViewState {
    data object Loading : DetailsViewState
    data class Ready(
        val product: ProductPresentationModel,
    ) : DetailsViewState

    data object Failure : DetailsViewState
}

sealed interface DetailsViewEvent : ViewEvent {
    data class ToggleFavorite(
        val product: ProductPresentationModel,
    ) : DetailsViewEvent

    data object Back : DetailsViewEvent
}

private sealed interface InternalDetailsViewEvent : DetailsViewEvent {
    data class ProductLoaded(
        val product: ProductPresentationModel,
    ) : InternalDetailsViewEvent

    data object FailedToLoad : InternalDetailsViewEvent
}

sealed interface DetailsSideEffect : ViewSideEffect {
    sealed interface Navigation : DetailsSideEffect {
        data object NavigateUp : Navigation
    }
}
