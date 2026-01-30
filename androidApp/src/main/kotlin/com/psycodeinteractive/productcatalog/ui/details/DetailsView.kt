package com.psycodeinteractive.productcatalog.ui.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import com.psycodeinteractive.productcatalog.R
import com.psycodeinteractive.productcatalog.design.component.FavoriteIcon
import com.psycodeinteractive.productcatalog.design.component.TopBar
import com.psycodeinteractive.productcatalog.design.theme.ProductCatalogTheme
import com.psycodeinteractive.productcatalog.presentation.feature.details.DetailsSideEffect.Navigation
import com.psycodeinteractive.productcatalog.presentation.feature.details.DetailsViewEvent
import com.psycodeinteractive.productcatalog.presentation.feature.details.DetailsViewEvent.ToggleFavorite
import com.psycodeinteractive.productcatalog.presentation.feature.details.DetailsViewModel
import com.psycodeinteractive.productcatalog.presentation.feature.details.DetailsViewState
import com.psycodeinteractive.productcatalog.presentation.feature.details.DetailsViewState.Failure
import com.psycodeinteractive.productcatalog.presentation.feature.details.DetailsViewState.Loading
import com.psycodeinteractive.productcatalog.presentation.feature.details.DetailsViewState.Ready
import com.psycodeinteractive.productcatalog.ui.View
import com.psycodeinteractive.productcatalog.ui.fixture.product1PresentationFixture
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Serializable
data class DetailsRoute(val id: Long) : NavKey

@Composable
fun DetailsView(
    productId: Long,
    viewModel: DetailsViewModel = koinViewModel(
        key = "details_$productId",
    ) {
        parametersOf(productId)
    },
    onNavigation: (Navigation) -> Unit,
) {
    View(viewModel) {
        val viewState by collectViewState()
        DetailsViewContent(viewState, ::processEvent)
        CollectSideEffects { sideEffect ->
            when (sideEffect) {
                is Navigation -> onNavigation(sideEffect)
            }
        }
    }
}

@Composable
private fun DetailsViewContent(
    viewState: DetailsViewState,
    processEvent: (DetailsViewEvent) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        TopBar(
            title = stringResource(R.string.product_details),
            onBackClick = { processEvent(DetailsViewEvent.Back) }
        )
        when (viewState) {
            Loading -> CircularProgressIndicator()
            is Ready -> DetailsReadyContent(viewState, processEvent)
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
private fun DetailsReadyContent(
    state: Ready,
    processEvent: (DetailsViewEvent) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(ProductCatalogTheme.spacing.full),
        verticalArrangement = Arrangement.spacedBy(ProductCatalogTheme.spacing.half),
    ) {
        Row {
            Text(
                modifier = Modifier.weight(1f),
                text = state.product.title,
                style = ProductCatalogTheme.typography.h1,
                color = MaterialTheme.colorScheme.primary,
            )
            FavoriteIcon(
                product = state.product,
                onFavoriteClick = { product ->
                    processEvent(ToggleFavorite(product))
                },
            )
        }
        Text(
            text = state.product.description,
            style = ProductCatalogTheme.typography.body1,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = "${stringResource(R.string.price)} ${state.product.price}",
            style = ProductCatalogTheme.typography.h2,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Preview
@Composable
fun DetailsViewContentReadyPreview() {
    DetailsViewContent(
        viewState = Ready(
            product = product1PresentationFixture
        ),
        processEvent = {}
    )
}

@Preview
@Composable
fun DetailsViewContentLoadingPreview() {
    DetailsViewContent(
        viewState = Loading,
        processEvent = {}
    )
}
