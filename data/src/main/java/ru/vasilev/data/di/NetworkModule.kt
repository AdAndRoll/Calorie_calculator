package ru.vasilev.data.di

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import ru.vasilev.data.network.rest.FakeInterceptor
import ru.vasilev.data.network.rest.RestApi
import ru.vasilev.data.network.soap.SoapApi
import javax.inject.Named
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
            .addInterceptor(FakeInterceptor())
            .build()
    }

    @Provides
    @Singleton
    @Named("RestRetrofit")
    fun provideRestRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://fake-api.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("SoapRetrofit")
    fun provideSoapRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://fake-api-soap.com/")
            .client(okHttpClient)
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideRestApi(@Named("RestRetrofit") retrofit: Retrofit): RestApi {
        return retrofit.create(RestApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSoapApi(@Named("SoapRetrofit") retrofit: Retrofit): SoapApi {
        return retrofit.create(SoapApi::class.java)
    }
}