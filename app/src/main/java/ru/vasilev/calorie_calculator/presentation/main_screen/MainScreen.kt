package ru.vasilev.calorie_calculator.presentation.main_screen

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // 1. Лаунчер для камеры (возвращает Bitmap)
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        // Передаем Bitmap напрямую во ViewModel
        bitmap?.let { viewModel.onImageSelected(it) }
    }

    // 2. Лаунчер для запроса разрешения камеры
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            Toast.makeText(context, "Доступ к камере отклонен", Toast.LENGTH_SHORT).show()
        }
    }

    // 3. Лаунчер для галереи (возвращает Uri)
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        // Передаем Uri напрямую во ViewModel
        uri?.let { viewModel.onImageSelected(it) }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Статус процесса (ТЗ 2.4.2 - Polling/Uploading)
            Text(
                text = uiState.statusText,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            if (uiState.result != null) {
                Spacer(modifier = Modifier.height(8.dp))
                // Здесь будет отображаться JSON результат или калории (ТЗ 2.1)
                Text(
                    text = "Результат: ${uiState.result}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Кнопка Галереи (ТЗ 2.3)
            Button(
                onClick = { galleryLauncher.launch("image/*") },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text("Выбрать из галереи")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Кнопка Камеры (ТЗ 2.3)
            Button(
                onClick = {
                    val permissionCheck = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    )
                    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                        cameraLauncher.launch(null)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text("Сделать фото")
            }

            // Индикация загрузки и опроса статуса (ТЗ 2.4.2)
            if (uiState.isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }
        }
    }
}