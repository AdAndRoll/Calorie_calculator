package ru.vasilev.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.vasilev.data.di.NetworkModule
import ru.vasilev.data.di.RepositoryModule
import ru.vasilev.domain.usecase.ProcessImageUseCase
import javax.inject.Singleton

@Singleton
@Component(modules = [
    NetworkModule::class,    // Из модуля :data (Retrofit)
    RepositoryModule::class  // Из модуля :data (Binds репозитория)
])
interface AppComponent {

    // Функция, которую будет вызывать модуль :ui (ViewModel),
    // чтобы получить логику обработки
    fun getProcessImageUseCase(): ProcessImageUseCase
    fun inject(mainActivity: ru.vasilev.calorie_calculator.MainActivity)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}