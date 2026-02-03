package ru.vasilev.data.di

import dagger.Binds
import dagger.Module
import ru.vasilev.data.repository.ImageRepositoryImpl
import ru.vasilev.domain.repository.ImageRepository
import javax.inject.Singleton

@Module
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindImageRepository(
        imageRepositoryImpl: ImageRepositoryImpl
    ): ImageRepository
}