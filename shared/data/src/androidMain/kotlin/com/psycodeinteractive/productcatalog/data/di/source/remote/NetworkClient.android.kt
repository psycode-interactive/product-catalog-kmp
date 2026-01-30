package com.psycodeinteractive.productcatalog.data.di.source.remote

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

actual val httpClientEngine: HttpClientEngine
    get() = OkHttp.create {
        config {
            followRedirects(true)
        }
    }
