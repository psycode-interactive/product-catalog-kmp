package com.psycodeinteractive.productcatalog.data.di.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import com.psycodeinteractive.productcatalog.data.di.source.PRODUCT_MAX_PAGE_SIZE
import com.psycodeinteractive.productcatalog.data.di.source.ProductPagingSource
import com.psycodeinteractive.productcatalog.data.di.source.ProductPagingType
import com.psycodeinteractive.productcatalog.data.di.source.local.ProductLocalDataSource
import com.psycodeinteractive.productcatalog.data.di.source.remote.model.ProductApiModel
import com.psycodeinteractive.productcatalog.data.di.source.remote.service.ProductApiService
import com.psycodeinteractive.productcatalog.domain.model.Product
import com.psycodeinteractive.productcatalog.domain.repository.ProductRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ProductRepositoryImpl(
    private val remoteDataSource: ProductApiService,
    private val localDataSource: ProductLocalDataSource,
    private val ioDispatcher: CoroutineDispatcher,
) : ProductRepository {

    override suspend fun getProducts() = Pager(
        config = PagingConfig(
            pageSize = PRODUCT_MAX_PAGE_SIZE,
            prefetchDistance = PRODUCT_MAX_PAGE_SIZE * 2,
            initialLoadSize = PRODUCT_MAX_PAGE_SIZE,
        ), pagingSourceFactory = {
            ProductPagingSource(remoteDataSource, ProductPagingType.Products)
        }
    ).flow.map { pagingData ->
        val favoriteIds = localDataSource.getFavoriteIds()
        pagingData.map {
            it.toDomain(isFavorite = it.id in favoriteIds)
        }
    }

    override suspend fun searchProducts(query: String) = Pager(
        config = PagingConfig(
            pageSize = PRODUCT_MAX_PAGE_SIZE,
            prefetchDistance = PRODUCT_MAX_PAGE_SIZE * 2,
            initialLoadSize = PRODUCT_MAX_PAGE_SIZE,
        ), pagingSourceFactory = {
            ProductPagingSource(remoteDataSource, ProductPagingType.Search(query))
        }
    ).flow.map { pagingData ->
        val favoriteIds = localDataSource.getFavoriteIds()
        pagingData.map {
            it.toDomain(isFavorite = it.id in favoriteIds)
        }
    }

    override suspend fun getProduct(id: Long) = withContext(ioDispatcher) {
        runCatching {
            val product = remoteDataSource.getProductDetails(id)
            val favoriteIds = localDataSource.getFavoriteIds()
            product.toDomain(isFavorite = product.id in favoriteIds)
        }
    }

    override fun getFavoriteProducts() = localDataSource.getFavorites()

    override suspend fun saveFavorite(product: Product) = withContext(ioDispatcher) {
        runCatching {
            localDataSource.saveFavorite(product)
        }
    }

    override suspend fun deleteFavorite(productId: Long) = withContext(ioDispatcher) {
        runCatching {
            localDataSource.deleteFavorite(productId)
        }
    }
}

fun ProductApiModel.toDomain(
    isFavorite: Boolean
) = Product(
    id = id,
    title = title,
    description = description,
    price = price,
    isFavorite = isFavorite
)
