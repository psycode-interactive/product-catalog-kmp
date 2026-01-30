package com.psycodeinteractive.productcatalog

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.psycodeinteractive.productcatalog.presentation.feature.details.DetailsSideEffect
import com.psycodeinteractive.productcatalog.presentation.feature.favorites.FavoritesSideEffect
import com.psycodeinteractive.productcatalog.presentation.feature.products.ProductsSideEffect
import com.psycodeinteractive.productcatalog.ui.details.DetailsRoute
import com.psycodeinteractive.productcatalog.ui.details.DetailsView
import com.psycodeinteractive.productcatalog.ui.favorites.FavoritesRoute
import com.psycodeinteractive.productcatalog.ui.favorites.FavoritesView
import com.psycodeinteractive.productcatalog.ui.products.ProductsRoute
import com.psycodeinteractive.productcatalog.ui.products.ProductsView

@Composable
@Preview
fun App() {
    MaterialTheme {
        val backStack = rememberNavBackStack(ProductsRoute)

        val navigateUp: () -> Unit = { backStack.removeLastOrNull() }
        val navigateTo = { route: NavKey -> backStack.add(route) }

        val entryProvider = entryProvider {
            entry<ProductsRoute> {
                ProductsView { navigation ->
                    when (navigation) {
                        is ProductsSideEffect.Navigation.NavigateToProductDetails -> {
                            navigateTo(DetailsRoute(navigation.productId))
                        }

                        ProductsSideEffect.Navigation.NavigateToFavorites -> {
                            navigateTo(FavoritesRoute)
                        }
                    }
                }
            }
            entry<DetailsRoute> { key ->
                DetailsView(key.id) { navigation ->
                    when (navigation) {
                        DetailsSideEffect.Navigation.NavigateUp -> navigateUp()
                    }
                }
            }
            entry<FavoritesRoute> {
                FavoritesView { navigation ->
                    when (navigation) {
                        is FavoritesSideEffect.Navigation.NavigateToProductDetails -> {
                            navigateTo(DetailsRoute(navigation.productId))
                        }
                        FavoritesSideEffect.Navigation.NavigateUp -> navigateUp()
                    }
                }
            }
        }

        NavDisplay(
            backStack = backStack,
            onBack = navigateUp,
            entryProvider = entryProvider,
        )
    }
}
