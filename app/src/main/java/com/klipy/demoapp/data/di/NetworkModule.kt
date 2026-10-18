package com.klipy.demoapp.data.di

import com.google.gson.GsonBuilder
import com.klipy.demoapp.BuildConfig
import com.klipy.demoapp.data.dto.MediaItemDto
import com.klipy.demoapp.data.dto.deserializer.MediaItemDtoDeserializer
import com.klipy.demoapp.data.infrastructure.interceptor.AdsQueryParametersInterceptor
import okhttp3.OkHttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    single {
        GsonBuilder()
            .registerTypeAdapter(MediaItemDto::class.java, MediaItemDtoDeserializer())
            .create()
    }

    single {
        val secretKey: String = get(named("secretKey"))
        Retrofit.Builder()
            .baseUrl("https://api.klipy.com/api/v1/$secretKey/")
            .client(get())
            .addConverterFactory(GsonConverterFactory.create(get()))
            .build()
    }

    single {
        OkHttpClient.Builder()
            .addInterceptor(get<AdsQueryParametersInterceptor>())
            .readTimeout(30, TimeUnit.SECONDS)
            .connectTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single<AdsQueryParametersInterceptor> {
        AdsQueryParametersInterceptor(get(), get())
    }

    // A local build can supply KLIPY_API_KEY; otherwise use the existing cached demo feed.
    single(named("secretKey")) {
        BuildConfig.KLIPY_API_KEY.ifEmpty {
            "Y9qk261ZOzIZMj6Y8OYOMsDcK6we3rHfYOvgZX8rfAIHorqidaM5RjTD0gQfvdnY"
        }
    }
}
