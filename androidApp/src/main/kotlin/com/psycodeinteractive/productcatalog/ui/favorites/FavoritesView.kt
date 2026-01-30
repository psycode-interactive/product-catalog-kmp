package com.psycodeinteractive.productcatalog.ui.favorites

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import com.psycodeinteractive.productcatalog.R
import com.psycodeinteractive.productcatalog.design.component.LoadingIndicator
import com.psycodeinteractive.productcatalog.design.component.ProductItem
import com.psycodeinteractive.productcatalog.design.component.TopBar
import com.psycodeinteractive.productcatalog.design.theme.ProductCatalogTheme
import com.psycodeinteractive.productcatalog.presentation.feature.favorites.FavoritesSideEffect.Navigation
import com.psycodeinteractive.productcatalog.presentation.feature.favorites.FavoritesViewEvent
import com.psycodeinteractive.productcatalog.presentation.feature.favorites.FavoritesViewEvent.FavoriteToggled
import com.psycodeinteractive.productcatalog.presentation.feature.favorites.FavoritesViewEvent.GoToDetails
import com.psycodeinteractive.productcatalog.presentation.feature.favorites.FavoritesViewModel
import com.psycodeinteractive.productcatalog.presentation.feature.favorites.FavoritesViewState
import com.psycodeinteractive.productcatalog.presentation.feature.favorites.FavoritesViewState.Failure
import com.psycodeinteractive.productcatalog.presentation.feature.favorites.FavoritesViewState.Loading
import com.psycodeinteractive.productcatalog.presentation.feature.favorites.FavoritesViewState.Ready
import com.psycodeinteractive.productcatalog.ui.View
import com.psycodeinteractive.productcatalog.ui.fixture.productsPresentationFixtures
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

@Serializable
data object FavoritesRoute : NavKey

@Composable
fun FavoritesView(
    viewModel: FavoritesViewModel = koinViewModel(),
    onNavigation: (Navigation) -> Unit,
) {
    View(viewModel) {
        val viewState by collectViewState()
        FavoritesViewContent(viewState, ::processEvent)
        CollectSideEffects { sideEffect ->
            when (sideEffect) {
                is Navigation -> onNavigation(sideEffect)
            }
        }
    }
}

@Composable
private fun FavoritesViewContent(
    viewState: FavoritesViewState,
    processEvent: (FavoritesViewEvent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        TopBar(
            title = stringResource(R.string.favorites),
            onBackClick = { processEvent(FavoritesViewEvent.Back) }
        )
        when (viewState) {
            Loading -> LoadingIndicator()
            is Ready -> FavoritesReadyContent(viewState, processEvent)
            Failure -> Text(
                modifier = Modifier.padding(ProductCatalogTheme.spacing.full),
                text = stringResource(R.string.failure),
                style = ProductCatalogTheme.typography.h2,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Composable
private fun FavoritesReadyContent(
    state: Ready,
    processEvent: (FavoritesViewEvent) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
    ) {
        items(
            items = state.products,
            key = { product -> product.id }
        ) { item ->
            ProductItem(
                product = item,
                onFavoriteClick = { product ->
                    processEvent(FavoriteToggled(product))
                },
                onItemClick = { product ->
                    processEvent(GoToDetails(product))
                }
            )
        }
        if (state.products.isEmpty()) {
            item {
                Text(
                    modifier = Modifier.padding(ProductCatalogTheme.spacing.full),
                    text = stringResource(R.string.empty),
                    style = ProductCatalogTheme.typography.h3,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoritesViewContentPreview() {
    ProductCatalogTheme {
        FavoritesViewContent(
            viewState = Ready(
                products = productsPresentationFixtures
            ),
            processEvent = {}
        )
    }
}
