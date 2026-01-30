package com.psycodeinteractive.productcatalog.design.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.MaterialTheme
import com.psycodeinteractive.productcatalog.presentation.model.ProductPresentationModel
import com.psycodeinteractive.productcatalog.design.theme.ProductCatalogTheme

@Composable
fun LazyItemScope.ProductItem(
    product: ProductPresentationModel,
    onFavoriteClick: (product: ProductPresentationModel) -> Unit,
    onItemClick: (product: ProductPresentationModel) -> Unit,
) {
    Row(
        modifier = Modifier
            .animateItem()
            .fillMaxWidth()
            .padding(ProductCatalogTheme.spacing.full)
            .clickable(onClick = { onItemClick(product) }),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = product.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = ProductCatalogTheme.typography.h3,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = product.description,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                style = ProductCatalogTheme.typography.body2,
                color = MaterialTheme.colorScheme.secondary,
            )
        }

        FavoriteIcon(
            product = product,
            onFavoriteClick = onFavoriteClick,
        )
    }
}
