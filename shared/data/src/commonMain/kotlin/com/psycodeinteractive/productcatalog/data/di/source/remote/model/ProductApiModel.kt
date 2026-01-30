package com.psycodeinteractive.productcatalog.data.di.source.remote.model

import kotlinx.serialization.Serializable

@Serializable
data class ProductApiModel(
    val id: Long,
    val title: String,
    val description: String,
    val category: String,
    val price: Double,
    val discountPercentage: Double,
    val rating: Double,
    val stock: Long,
    val tags: List<String>,
    val sku: String,
    val weight: Long,
//    val dimensions: Dimensions,
    val warrantyInformation: String,
    val shippingInformation: String,
    val availabilityStatus: String,
//    val reviews: List<Review>,
    val returnPolicy: String,
    val minimumOrderQuantity: Long,
//    val meta: Meta,
    val images: List<String>,
    val thumbnail: String,
)
