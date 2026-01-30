package com.psycodeinteractive.productcatalog.data.di

import androidx.room.Room
import com.psycodeinteractive.productcatalog.data.di.repository.ProductRepositoryImpl
import com.psycodeinteractive.productcatalog.data.di.source.ProductPagingSource
import com.psycodeinteractive.productcatalog.data.di.source.local.AppDatabase
import com.psycodeinteractive.productcatalog.data.di.source.local.FavoriteProductDao
import com.psycodeinteractive.productcatalog.data.di.source.local.ProductLocalDataSource
import com.psycodeinteractive.productcatalog.data.di.source.local.ProductLocalDataSourceImpl
import com.psycodeinteractive.productcatalog.data.di.source.local.provideDatabaseBuilder
import com.psycodeinteractive.productcatalog.data.di.source.remote.httpClientEngine
import com.psycodeinteractive.productcatalog.data.di.source.remote.service.ProductApiService
import com.psycodeinteractive.productcatalog.data.di.source.remote.service.ProductApiServiceImpl
import com.psycodeinteractive.productcatalog.domain.repository.ProductRepository
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType.Application.Json
import io.ktor.http.URLProtocol.Companion.HTTPS
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

private const val API_BASE_URL = "dummyjson.com"

val dataModule = module {
    single<CoroutineDispatcher> { Dispatchers.IO }
    single<HttpClient> {
        HttpClient(httpClientEngine) {
            defaultRequest {
                contentType(Json)
                host = API_BASE_URL
                port = 0
                url {
                    protocol = HTTPS
                }
            }

            install(ContentNegotiation) {
                json(get())
            }
        }
    }
    single<ProductRepository>{ ProductRepositoryImpl(get(), get(), get()) }
    single<ProductApiService>{ ProductApiServiceImpl(get()) }

    single<AppDatabase> {
        provideDatabaseBuilder().build()
    }
    single<FavoriteProductDao> { get<AppDatabase>().favoriteProductDao() }

    single<Json> {
        Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        }
    }
    single<ProductLocalDataSource> { ProductLocalDataSourceImpl(get()) }
    singleOf(::ProductPagingSource)
}
