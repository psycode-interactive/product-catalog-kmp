package com.psycodeinteractive.productcatalog.data.di.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.psycodeinteractive.productcatalog.domain.model.Product

@Entity(tableName = "favorite_products")
data class FavoriteProductDatabaseModel(
    @PrimaryKey val id: Long,
    val title: String,
    val description: String,
    val price: Double
)

fun FavoriteProductDatabaseModel.toDomain() = Product(
    id = id,
    title = title,
    description = description,
    price = price,
    isFavorite = true
)

fun Product.toDatabase() = FavoriteProductDatabaseModel(
    id = id,
    title = title,
    description = description,
    price = price
)
