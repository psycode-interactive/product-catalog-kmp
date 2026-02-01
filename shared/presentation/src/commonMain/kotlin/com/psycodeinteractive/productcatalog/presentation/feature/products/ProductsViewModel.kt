package com.psycodeinteractive.productcatalog.presentation.feature.products

import androidx.lifecycle.viewModelScope
import androidx.paging.ItemSnapshotList
import androidx.paging.PagingData
import androidx.paging.PagingDataEvent
import androidx.paging.PagingDataPresenter
import androidx.paging.cachedIn
import androidx.paging.map
import com.psycodeinteractive.productcatalog.domain.model.Product
import com.psycodeinteractive.productcatalog.domain.usecase.GetFavoriteProductsUseCase
import com.psycodeinteractive.productcatalog.domain.usecase.GetProductsUseCase
import com.psycodeinteractive.productcatalog.domain.usecase.SearchProductsUseCase
import com.psycodeinteractive.productcatalog.domain.usecase.ToggleFavoriteProductUseCase
import com.psycodeinteractive.productcatalog.presentation.BaseViewModel
import com.psycodeinteractive.productcatalog.presentation.ViewEvent
import com.psycodeinteractive.productcatalog.presentation.ViewSideEffect
import com.psycodeinteractive.productcatalog.presentation.ViewState
import com.psycodeinteractive.productcatalog.presentation.feature.products.InternalProductsViewEvent.ProductsPagingLoaded
import com.psycodeinteractive.productcatalog.presentation.feature.products.InternalProductsViewEvent.SearchCompleted
import com.psycodeinteractive.productcatalog.presentation.feature.products.ProductsSideEffect.Navigation
import com.psycodeinteractive.productcatalog.presentation.feature.products.ProductsViewEvent.GoToDetails
import com.psycodeinteractive.productcatalog.presentation.feature.products.ProductsViewEvent.GoToFavorites
import com.psycodeinteractive.productcatalog.presentation.feature.products.ProductsViewEvent.QueryChanged
import com.psycodeinteractive.productcatalog.presentation.feature.products.ProductsViewEvent.ToggleFavorite
import com.psycodeinteractive.productcatalog.presentation.feature.products.ProductsViewState.Failure
import com.psycodeinteractive.productcatalog.presentation.feature.products.ProductsViewState.Loading
import com.psycodeinteractive.productcatalog.presentation.feature.products.ProductsViewState.Ready
import com.psycodeinteractive.productcatalog.presentation.feature.products.ProductsViewState.Search
import com.psycodeinteractive.productcatalog.presentation.model.ProductPresentationModel
import com.psycodeinteractive.productcatalog.presentation.model.toDomain
import com.psycodeinteractive.productcatalog.presentation.model.toPresentation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal const val SEARCH_DEBOUNCE_MS = 450L

class ProductsViewModel(
    private val getProductsUseCase: GetProductsUseCase,
    private val searchProductsUseCase: SearchProductsUseCase,
    private val toggleFavoriteProductUseCase: ToggleFavoriteProductUseCase,
    getFavoriteProductsUseCase: GetFavoriteProductsUseCase,
) : BaseViewModel<ProductsViewState, ProductsViewEvent, ProductsSideEffect>() {

    override val initialViewState: ProductsViewState = Loading

    private var productsPagingFlow: Flow<PagingData<Product>>? = null
    private val favoritesFlow: Flow<List<Product>> = getFavoriteProductsUseCase()

    init {
        observeSearch()
        loadProducts()
    }

    private fun observeSearch() {
        viewState
            .filterIsInstance<Search>()
            .map { it.query }
            .debounce(SEARCH_DEBOUNCE_MS)
            .distinctUntilChanged()
            .mapLatest { query ->
                if (query.isBlank()) loadProducts() else performSearch(query)
            }
            .launchIn(viewModelScope)
    }

    override fun reduceState(
        currentState: ProductsViewState,
        event: ProductsViewEvent,
    ): ProductsViewState = when (event) {
        is ProductsPagingLoaded -> Ready(event.products)
        is SearchCompleted -> onSearchCompleted(currentState, event)
        is QueryChanged -> onQueryChanged(currentState, event)
        is ToggleFavorite -> onToggleFavorite(currentState, event)
        is GoToDetails -> onGoToDetails(currentState, event)
        InternalProductsViewEvent.FailedToLoad -> Failure
        GoToFavorites -> onGoToFavorites(currentState)
    }

    private fun loadProducts() {
        viewModelScope.launch {
            val basePaging = productsPagingFlow
                ?: getProductsUseCase().cachedIn(viewModelScope)
                    .also { productsPagingFlow = it }

            val result = combineFavoriteProducts(basePaging, favoritesFlow)

            processEvent(ProductsPagingLoaded(result))
        }
    }

    private fun performSearch(query: String) {
        viewModelScope.launch {
            val basePaging = searchProductsUseCase(query).cachedIn(viewModelScope)

            val result = combineFavoriteProducts(basePaging, favoritesFlow)

            processEvent(SearchCompleted(result))
        }
    }

    private fun combineFavoriteProducts(
        products: Flow<PagingData<Product>>,
        favorites: Flow<List<Product>>
    ) = combine(products, favorites) { pagingData, favorites ->
        pagingData
            .map { item ->
                val isFavorite = item.id in favorites.map { it.id }
                item.copy(isFavorite = isFavorite)
            }
            .map(Product::toPresentation)
    }

    private fun onToggleFavorite(
        currentState: ProductsViewState,
        event: ToggleFavorite,
    ): ProductsViewState {
        viewModelScope.launch {
            toggleFavoriteProductUseCase(event.product.toDomain())
        }
        return currentState
    }

    private fun onQueryChanged(
        currentState: ProductsViewState,
        event: QueryChanged,
    ): ProductsViewState {
        return when (currentState) {
            is Search -> currentState.copy(query = event.query, isLoading = true)
            else -> Search(query = event.query, isLoading = true)
        }
    }

    private fun onSearchCompleted(
        currentState: ProductsViewState,
        event: SearchCompleted
    ): ProductsViewState {
        return (currentState as Search).copy(
            products = event.products,
            isLoading = false
        )
    }

    private fun onGoToDetails(
        currentState: ProductsViewState,
        event: GoToDetails,
    ): ProductsViewState {
        postSideEffect(Navigation.NavigateToProductDetails(event.product.id))
        return currentState
    }

    private fun onGoToFavorites(currentState: ProductsViewState): ProductsViewState {
        postSideEffect(Navigation.NavigateToFavorites)
        return currentState
    }

    //region used by iOS - it could look something like this
    private val pagingDataPresenter =
        object : PagingDataPresenter<ProductPresentationModel>() {
            override suspend fun presentPagingDataEvent(
                event: PagingDataEvent<ProductPresentationModel>
            ) {
                updateProductsSnapshotList()
            }
        }

    val productsSnapshotList: MutableStateFlow<ItemSnapshotList<ProductPresentationModel>> =
        MutableStateFlow(pagingDataPresenter.snapshot())

    val loadStateFlow = pagingDataPresenter.loadStateFlow

    init {
        viewModelScope.launch {
            viewState
                .map { state ->
                    when (state) {
                        is Ready -> state.products
                        is Search -> state.products
                        else -> null
                    }
                }
                .distinctUntilChanged()
                .collectLatest { pagingFlow ->
                    pagingFlow?.collectLatest {
                        pagingDataPresenter.collectFrom(it)
                    }
                }
        }
    }

    private fun updateProductsSnapshotList() {
        productsSnapshotList.update {
            pagingDataPresenter.snapshot()
        }
    }

    fun loadMore() {
        val index = pagingDataPresenter.size - 1
        if (index >= 0) {
            pagingDataPresenter[index]
        }
    }

    fun retry() {
        pagingDataPresenter.retry()
    }
    //endregion

}

sealed interface ProductsViewState : ViewState {
    data object Loading : ProductsViewState
    data class Search(
        val query: String,
        val products: Flow<PagingData<ProductPresentationModel>> = emptyFlow(),
        val isLoading: Boolean = false,
    ) : ProductsViewState

    data class Ready(
        val products: Flow<PagingData<ProductPresentationModel>> = emptyFlow(),
    ) : ProductsViewState

    data object Failure : ProductsViewState
}

sealed interface ProductsViewEvent : ViewEvent {
    data class QueryChanged(val query: String) : ProductsViewEvent
    data class ToggleFavorite(val product: ProductPresentationModel) : ProductsViewEvent
    data object GoToFavorites : ProductsViewEvent
    data class GoToDetails(
        val product: ProductPresentationModel,
    ) : ProductsViewEvent

}

private sealed interface InternalProductsViewEvent : ProductsViewEvent {
    data class ProductsPagingLoaded(
        val products: Flow<PagingData<ProductPresentationModel>>,
    ) : InternalProductsViewEvent

    data class SearchCompleted(
        val products: Flow<PagingData<ProductPresentationModel>>,
    ) : InternalProductsViewEvent

    data object FailedToLoad : InternalProductsViewEvent
}

sealed interface ProductsSideEffect : ViewSideEffect {
    sealed interface Navigation : ProductsSideEffect {
        data class NavigateToProductDetails(val productId: Long) : Navigation
        data object NavigateToFavorites : Navigation
    }
}
