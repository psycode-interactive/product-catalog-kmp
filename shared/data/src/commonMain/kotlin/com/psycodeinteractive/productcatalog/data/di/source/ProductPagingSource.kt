package com.psycodeinteractive.productcatalog.data.di.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.psycodeinteractive.productcatalog.data.di.source.remote.model.ProductApiModel
import com.psycodeinteractive.productcatalog.data.di.source.remote.service.ProductApiService

internal const val PRODUCT_MAX_PAGE_SIZE = 10

sealed interface ProductPagingType {
    data object Products : ProductPagingType
    data class Search(val query: String) : ProductPagingType
}

class ProductPagingSource(
    private val remoteDataSource: ProductApiService,
    private val type: ProductPagingType,
) : PagingSource<Int, ProductApiModel>() {

    override suspend fun load(params: LoadParams<Int>) = try {
        val skip = params.key ?: 0
        val limit = params.loadSize
        val responseData = when(type) {
            ProductPagingType.Products -> remoteDataSource.getProducts(limit, skip)
            is ProductPagingType.Search -> remoteDataSource.searchProducts(type.query, limit, skip)
        }
        val products = responseData.products
        val nextKey = if (skip + products.size >= responseData.total) {
            null // reached the end
        } else {
            skip + limit
        }

        val prevKey = if (skip == 0) null else maxOf(skip - limit, 0)

        LoadResult.Page(
            data = products,
            prevKey = prevKey,
            nextKey = nextKey
        )
    } catch (e: Exception) {
        e.printStackTrace()
        LoadResult.Error(e)
    }

    override fun getRefreshKey(state: PagingState<Int, ProductApiModel>): Int? {
        return state.anchorPosition?.let { position ->
            state.closestPageToPosition(position)?.let { page ->
                page.prevKey?.plus(page.data.size)
                    ?: page.nextKey?.minus(page.data.size)
            }
        }
    }

//    override fun getRefreshKey(state: PagingState<Int, ProductApiModel>): Int? {
//        // Anchor position = the last accessed index in the list
//        val anchorPosition = state.anchorPosition ?: return null
//
//        // Find the page closest to the anchor position
//        val closestPage = state.closestPageToPosition(anchorPosition) ?: return null
//
//        // Compute new key (skip/offset) for refreshing
//        return closestPage.prevKey?.plus(closestPage.data.size)
//            ?: closestPage.nextKey?.minus(closestPage.data.size)
//    }
}