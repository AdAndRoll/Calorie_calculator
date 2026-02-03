package ru.vasilev.calorie_calculator.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.vasilev.calorie_calculator.MainActivity
import ru.vasilev.calorie_calculator.presentation.main_screen.MainViewModel
import ru.vasilev.data.di.NetworkModule
import ru.vasilev.data.di.RepositoryModule
import ru.vasilev.domain.usecase.ProcessImageUseCase
import javax.inject.Singleton

@Singleton
@Component(modules = [
    NetworkModule::class,
    RepositoryModule::class
])
interface AppComponent {

    // 1. Добавляем этот метод, чтобы MainActivity могла получить готовую вьюмодель
    fun getMainViewModel(): MainViewModel

    // Твой текущий метод (можно оставить для тестов)
    fun getProcessImageUseCase(): ProcessImageUseCase

    // Метод для классической инъекции (если будешь использовать @Inject в Activity)
    fun inject(mainActivity: MainActivity)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}