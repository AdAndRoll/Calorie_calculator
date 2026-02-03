package ru.vasilev.calorie_calculator

import android.app.Application
import ru.vasilev.calorie_calculator.di.AppComponent
// Импорт должен указывать на сгенерированный код в том же пакете, что и AppComponent
import ru.vasilev.calorie_calculator.di.DaggerAppComponent

class App : Application() {

    // Сделаем доступ удобнее через статическое поле (опционально, но часто используется)
    companion object {
        lateinit var appComponent: AppComponent
            private set
    }

    override fun onCreate() {
        super.onCreate()

        // Инициализируем компонент через Factory
        appComponent = DaggerAppComponent.factory()
            .create(this) // Передаем сам экземпляр Application (он же Context)
    }
}