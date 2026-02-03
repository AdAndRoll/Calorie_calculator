package ru.vasilev.calorie_calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import ru.vasilev.calorie_calculator.presentation.main_screen.MainScreen
import ru.vasilev.calorie_calculator.presentation.main_screen.MainViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Включаем Edge-to-Edge для современного вида (прозрачные бары)
        enableEdgeToEdge()

        // 2. Получаем ViewModel из нашего Dagger-компонента
        // Примечание: Убедись, что в интерфейсе AppComponent прописан метод:
        // fun getMainViewModel(): MainViewModel
        val viewModel = App.appComponent.getMainViewModel()

        setContent {
            // 3. Устанавливаем общую тему приложения
            MaterialTheme {
                // 4. Запускаем наш чистый экран, передавая в него вьюмодель
                MainScreen(viewModel = viewModel)
            }
        }
    }
}