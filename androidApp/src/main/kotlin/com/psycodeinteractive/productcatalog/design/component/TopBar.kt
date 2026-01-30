package com.psycodeinteractive.productcatalog.design.component

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.psycodeinteractive.productcatalog.R
import com.psycodeinteractive.productcatalog.design.theme.ProductCatalogTheme

@Composable
fun TopBar(
    title: String?,
    onBackClick: () -> Unit,
) {
    TopAppBar(
        title = {
            title?.let {
                Text(
                    text = it,
                    style = ProductCatalogTheme.typography.h1,
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onBackClick
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = null,
                )
            }
        }
    )
}
