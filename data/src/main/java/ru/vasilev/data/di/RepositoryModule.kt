package ru.vasilev.data.di

import dagger.Binds
import dagger.Module
import ru.vasilev.data.repository.RestRepositoryImpl
import ru.vasilev.domain.repository.ImageRepository
import javax.inject.Singleton

@Module
abstract class RepositoryModule {

    @Binds
    @Singleton
    // Эта функция говорит Dagger: "Если кому-то нужен ImageRepository,
    // дай ему RestRepositoryImpl"
    abstract fun bindImageRepository(
        restRepositoryImpl: RestRepositoryImpl
    ): ImageRepository
}