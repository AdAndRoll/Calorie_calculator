package ru.vasilev.calorie_calculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ru.vasilev.calorie_calculator.presentation.main_screen.MainScreen
import ru.vasilev.calorie_calculator.presentation.main_screen.MainViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Используем встроенный делегат viewModels и передаем фабрику
        val viewModel: MainViewModel by viewModels {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return App.appComponent.getMainViewModel() as T
                }
            }
        }

        setContent {
            MaterialTheme {
                MainScreen(viewModel = viewModel)
            }
        }
    }
}