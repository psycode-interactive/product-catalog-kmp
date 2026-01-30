package com.psycodeinteractive.productcatalog.ui.products

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AppBarWithSearch
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults.InputField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.psycodeinteractive.productcatalog.R
import com.psycodeinteractive.productcatalog.presentation.feature.products.ProductsSideEffect.Navigation
import com.psycodeinteractive.productcatalog.presentation.feature.products.ProductsViewEvent
import com.psycodeinteractive.productcatalog.presentation.feature.products.ProductsViewEvent.GoToDetails
import com.psycodeinteractive.productcatalog.presentation.feature.products.ProductsViewEvent.GoToFavorites
import com.psycodeinteractive.productcatalog.presentation.feature.products.ProductsViewEvent.QueryChanged
import com.psycodeinteractive.productcatalog.presentation.feature.products.ProductsViewEvent.ToggleFavorite
import com.psycodeinteractive.productcatalog.presentation.feature.products.ProductsViewModel
import com.psycodeinteractive.productcatalog.presentation.feature.products.ProductsViewState
import com.psycodeinteractive.productcatalog.presentation.feature.products.ProductsViewState.Failure
import com.psycodeinteractive.productcatalog.presentation.feature.products.ProductsViewState.Loading
import com.psycodeinteractive.productcatalog.presentation.feature.products.ProductsViewState.Ready
import com.psycodeinteractive.productcatalog.presentation.feature.products.ProductsViewState.Search
import com.psycodeinteractive.productcatalog.presentation.model.ProductPresentationModel
import com.psycodeinteractive.productcatalog.ui.View
import com.psycodeinteractive.productcatalog.design.component.LoadingIndicator
import com.psycodeinteractive.productcatalog.design.component.ProductItem
import com.psycodeinteractive.productcatalog.design.theme.ProductCatalogTheme
import com.psycodeinteractive.productcatalog.ui.fixture.productsPresentationFixtures
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object ProductsRoute : NavKey

@Composable
fun ProductsView(
    viewModel: ProductsViewModel = koinViewModel(),
    onNavigation: (Navigation) -> Unit,
) {
    View(viewModel) {
        val viewState by collectViewState()
        ProductsViewContent(viewState, ::processEvent)
        CollectSideEffects { sideEffect ->
            when (sideEffect) {
                is Navigation -> onNavigation(sideEffect)
            }
        }
    }
}

@Composable
private fun ProductsViewContent(
    viewState: ProductsViewState,
    processEvent: (ProductsViewEvent) -> Unit,
) {
    val inputField =
        @Composable {
            InputField(
                modifier = Modifier.fillMaxWidth(),
                query = (viewState as? Search)?.query.orEmpty(),
                onQueryChange = { query ->
                    processEvent(QueryChanged(query))
                },
                onSearch = {},
                expanded = false,
                onExpandedChange = {},
                placeholder = {
                    Text(text = stringResource(R.string.search_product))
                },
            )
        }
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        val searchBarState = rememberSearchBarState()
        AppBarWithSearch(
            state = searchBarState,
            inputField = inputField,
            actions = {
                IconButton(
                    onClick = { processEvent(GoToFavorites) }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.favorite),
                        contentDescription = null,
                    )
                }
            },
        )

        when (viewState) {
            Loading -> LoadingIndicator()
            is Ready -> ProductList(viewState.products, processEvent)
            is Search -> {
                if (viewState.isLoading) {
                    LoadingIndicator()
                }
                ProductList(viewState.products, processEvent)
            }

            Failure -> Text(
                modifier = Modifier.padding(ProductCatalogTheme.spacing.full),
                text = stringResource(R.string.failure),
                style = ProductCatalogTheme.typography.h2,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun ProductList(
    products: Flow<PagingData<ProductPresentationModel>>,
    processEvent: (ProductsViewEvent) -> Unit,
) {
    val products = products.collectAsLazyPagingItems()
    LazyColumn {
        items(
            count = products.itemCount,
            key = { index -> (products[index] as ProductPresentationModel).id },
        ) { index ->
            ProductItem(
                product = products[index] as ProductPresentationModel,
                onFavoriteClick = { product ->
                    processEvent(ToggleFavorite(product))
                },
                onItemClick = { product ->
                    processEvent(GoToDetails(product))
                }
            )
        }
        if (products.itemCount == 0 && products.loadState.refresh != LoadState.Loading) {
            item {
                Text(
                    modifier = Modifier.padding(ProductCatalogTheme.spacing.full),
                    text = stringResource(R.string.no_results),
                    style = ProductCatalogTheme.typography.h3,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun ProductsViewContentPreview() {
    ProductCatalogTheme {
        ProductsViewContent(
            viewState = Ready(
                products = flowOf(PagingData.from(productsPresentationFixtures))
            ),
            processEvent = {}
        )
    }
}
