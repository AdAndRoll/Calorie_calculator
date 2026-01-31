package ru.vasilev.data.di

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.vasilev.data.network.rest.FakeInterceptor
import ru.vasilev.data.network.rest.RestApi
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor(FakeInterceptor()) // Наш мок
            .build()
    }

    @Provides
    @Singleton
    fun provideRestApi(okHttpClient: OkHttpClient): RestApi {
        return Retrofit.Builder()
            .baseUrl("https://fake-api.com/") // Любой URL, интерцептор его подменит
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RestApi::class.java)
    }
}