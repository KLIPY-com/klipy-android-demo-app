package com.klipy.demoapp.data.di

import com.klipy.demoapp.data.KlipyRepositoryImpl
import com.klipy.demoapp.data.datasource.ClipsDataSource
import com.klipy.demoapp.data.infrastructure.DeviceInfoProvider
import com.klipy.demoapp.data.infrastructure.DeviceInfoProviderImpl
import com.klipy.demoapp.data.datasource.GifsDataSource
import com.klipy.demoapp.data.datasource.HealthCheckDataSource
import com.klipy.demoapp.data.datasource.HealthCheckDataSourceImpl
import com.klipy.demoapp.data.datasource.MediaDataSource
import com.klipy.demoapp.data.datasource.MediaDataSourceFactory
import com.klipy.demoapp.data.datasource.MediaDataSourceFactoryImpl
import com.klipy.demoapp.data.datasource.MediaDataSourceManagerImpl
import com.klipy.demoapp.data.datasource.MediaDataSourceManager
import com.klipy.demoapp.data.datasource.StickersDataSource
import com.klipy.demoapp.data.infrastructure.AdvertisingInfoProvider
import com.klipy.demoapp.data.infrastructure.AdvertisingInfoProviderImpl
import com.klipy.demoapp.data.mapper.MediaItemMapper
import com.klipy.demoapp.data.mapper.MediaItemMapperImpl
import com.klipy.demoapp.data.service.ClipsService
import com.klipy.demoapp.data.service.GifService
import com.klipy.demoapp.data.service.StickersService
import com.klipy.demoapp.data.infrastructure.ApiCallHelper
import com.klipy.demoapp.data.infrastructure.ScreenMeasurementsProvider
import com.klipy.demoapp.data.infrastructure.ScreenMeasurementsProviderImpl
import com.klipy.demoapp.data.service.HealthCheckService
import com.klipy.demoapp.domain.KlipyRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val dataModule = module {
    single<GifService> {
        get<Retrofit>().create(GifService::class.java)
    }
    single<StickersService> {
        get<Retrofit>().create(StickersService::class.java)
    }
    single<ClipsService> {
        get<Retrofit>().create(ClipsService::class.java)
    }
    single<HealthCheckService> {
        get<Retrofit>().create(HealthCheckService::class.java)
    }

    factory<KlipyRepository> {
        KlipyRepositoryImpl(get(), get())
    }

    single<MediaItemMapper> { MediaItemMapperImpl() }

    factory<MediaDataSource>(named(GIFS_DS)) {
        GifsDataSource(get(), get(), get(), get())
    }
    factory<MediaDataSource>(named(STICKERS_DS)) {
        StickersDataSource(get(), get(), get(), get())
    }
    factory<MediaDataSource>(named(CLIPS_DS)) {
        ClipsDataSource(get(), get(), get(), get())
    }
    factory<HealthCheckDataSource> {
        HealthCheckDataSourceImpl(get(), get(), get())
    }

    factory<MediaDataSourceManager> {
        MediaDataSourceManagerImpl(get())
    }
    single<MediaDataSourceFactory> {
        MediaDataSourceFactoryImpl(getAll())
    }

    single<DeviceInfoProvider> { DeviceInfoProviderImpl(get()) }
    single<ScreenMeasurementsProvider> { ScreenMeasurementsProviderImpl() }
    single<AdvertisingInfoProvider> {
        AdvertisingInfoProviderImpl(androidApplication().applicationContext)
    }
    single<ApiCallHelper> { ApiCallHelper() }
}

private const val GIFS_DS = "gifsDataSource"
private const val CLIPS_DS = "stickersDataSource"
private const val STICKERS_DS = "clipsDataSource"