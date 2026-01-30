package com.psycodeinteractive.productcatalog.design.component

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import com.psycodeinteractive.productcatalog.R
import com.psycodeinteractive.productcatalog.presentation.model.ProductPresentationModel

@Composable
fun FavoriteIcon(
    product: ProductPresentationModel,
    onFavoriteClick: (product: ProductPresentationModel) -> Unit,
) {
    IconButton(
        onClick = { onFavoriteClick(product) },
        colors = IconButtonDefaults.iconButtonColors().copy(
            contentColor = MaterialTheme.colorScheme.tertiary,
        )
    ) {
        val icon = remember(product.isFavorite) {
            if (product.isFavorite) {
                R.drawable.favorite_filled
            } else {
                R.drawable.favorite
            }
        }
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
        )
    }
}
