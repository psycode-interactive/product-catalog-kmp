package com.psycodeinteractive.productcatalog.domain.model

data class Product(
    val id: Long,
    val title: String,
    val description: String,
    val isFavorite: Boolean,
    val price: Double,
) {
//    fun toggleFavorite() = copy(isFavorite = !isFavorite)
}
