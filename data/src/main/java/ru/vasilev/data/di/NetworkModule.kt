package ru.vasilev.data.di


import io.grpc.Channel

import dagger.Module
import dagger.Provides
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import ru.vasilev.data.grpc.ImageServiceGrpc
import ru.vasilev.data.network.rest.FakeInterceptor
import ru.vasilev.data.network.rest.RestApi
import ru.vasilev.data.network.soap.SoapApi
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS) // ТЗ 2.4.2: Таймаут запроса
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
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

    @Provides
    @Singleton
    fun provideGrpcChannel(): ManagedChannel {
        return ManagedChannelBuilder
            // Для реального сервера используй .forAddress("host", port).useTransportSecurity()
            // Для тестов или локальной разработки без SSL используй .usePlaintext()
            .forAddress("10.0.2.2", 50051) // Адрес эмулятора для доступа к localhost компа
            .usePlaintext()
            .build()
    }

    @Provides
    @Singleton
    fun provideImageServiceStub(channel: ManagedChannel): ImageServiceGrpc.ImageServiceStub {
        return ImageServiceGrpc.newStub(channel)
    }

}