package ru.vasilev.calorie_calculator

import android.app.Application
import ru.vasilev.di.AppComponent
import ru.vasilev.di.DaggerAppComponent

class App : Application() {

    // Граф зависимостей, который будет доступен во всем приложении
    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()

        // Инициализируем Dagger компонент
        // Если DaggerAppComponent подсвечен красным — нажми Build -> Rebuild Project
        appComponent = DaggerAppComponent.factory().create(applicationContext)
    }
}