package ru.vasilev.calorie_calculator.presentation.main_screen

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.vasilev.domain.model.ImageStatus
import ru.vasilev.domain.model.ProtocolType
import ru.vasilev.domain.usecase.ProcessImageUseCase
import ru.vasilev.util.ImageCompressor
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val processImageUseCase: ProcessImageUseCase,
    private val imageCompressor: ImageCompressor
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    /**
     * Принимает Any (Uri или Bitmap) и запускает процесс сжатия и отправки
     */
    fun onImageSelected(input: Any) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, statusText = "Сжатие изображения...")

            // 1. Сжатие согласно Пункту 2.3 ТЗ
            val bytes = when (input) {
                is Bitmap -> imageCompressor.compressFromBitmap(input)
                is Uri -> imageCompressor.compressFromUri(input)
                else -> null
            }

            if (bytes == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    statusText = "Ошибка оптимизации фото"
                )
                return@launch
            }

            // 2. Отправка и обработка состояний (Пункт 2.4.2 ТЗ)
            processImageUseCase(
                imageBytes = bytes,
                description = "Photo from App",
                protocol = ProtocolType.REST // Пока REST, скоро добавим SOAP
            ).collectLatest { status ->
                handleStatusUpdate(status)
            }
        }
    }

    private fun handleStatusUpdate(status: ImageStatus) {
        _uiState.value = when (status) {
            is ImageStatus.Idle -> _uiState.value.copy(
                isLoading = false,
                statusText = "Ожидание..."
            )
            is ImageStatus.Uploading -> _uiState.value.copy(
                isLoading = true,
                statusText = "Загрузка на сервер..."
            )
            is ImageStatus.Polling -> _uiState.value.copy(
                isLoading = true,
                statusText = "Опрос статуса (попытка ${status.retryCount})"
            )
            is ImageStatus.Success -> _uiState.value.copy(
                isLoading = false,
                statusText = "Анализ завершен",
                result = status.jsonResponse
            )
            is ImageStatus.Error -> _uiState.value.copy(
                isLoading = false,
                statusText = "Ошибка: ${status.message}"
            )
        }
    }
}