package com.example.notestrack.core.di

import okhttp3.Interceptor
import okhttp3.Response

class JwtIntercepter : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.run {
        proceed(
            request()
                .newBuilder().addHeader("Authorization", AppLevelModuleConstants.SERVER_KEY)
                .build()
        )
    }
}
