package ru.vasilev.calorie_calculator

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.vasilev.data.network.rest.RestApi
import ru.vasilev.domain.model.ProtocolType
import javax.inject.Inject
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Достаем UseCase напрямую из графа для теста
        val useCase = (application as App).appComponent.getProcessImageUseCase()

        setContent {
            val scope = rememberCoroutineScope()
            // Стейт для отображения результата на экране
            var resultText by remember { mutableStateOf("Нажми кнопку для теста API") }

            MaterialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = resultText, modifier = Modifier.padding(16.dp))

                            Button(onClick = {
                                resultText = "Отправка..."
                                scope.launch {
                                    // Вызываем цепочку: загрузка -> ожидание -> результат
                                    // Передаем пустой массив байтов как "картинку"
                                    useCase(
                                        imageBytes = ByteArray(0), // "Пустая" картинка для теста
                                        description = "Тестовое описание",
                                        protocol = ProtocolType.REST // Или другой тип, который есть в твоем домене
                                    ).collectLatest { result ->
                                        resultText = "Статус: ${result.javaClass.simpleName}\nДанные: $result"
                                        Log.d("TEST_API", "Result: $result")
                                    }
                                }
                            }) {
                                Text("Проверить загрузку фото")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainScreen(isApiReady: Boolean) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            val message = if (isApiReady) {
                "Dagger + Compose: RestApi готов! 🚀"
            } else {
                "Ошибка инициализации Dagger ❌"
            }

            Text(
                text = message,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}