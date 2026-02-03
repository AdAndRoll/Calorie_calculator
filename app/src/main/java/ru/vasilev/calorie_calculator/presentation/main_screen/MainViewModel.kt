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

    // Метод для переключения протокола
    fun onProtocolChanged(protocol: ProtocolType) {
        _uiState.value = _uiState.value.copy(selectedProtocol = protocol)
    }

    fun onImageSelected(input: Any) {
        viewModelScope.launch {
            val currentProtocol = _uiState.value.selectedProtocol // Берем из стейта

            _uiState.value = _uiState.value.copy(
                isLoading = true,
                statusText = "Сжатие для ${currentProtocol.name}...",
                result = null
            )

            val bytes = when (input) {
                is Bitmap -> imageCompressor.compressFromBitmap(input)
                is Uri -> imageCompressor.compressFromUri(input)
                else -> null
            }

            if (bytes == null) {
                _uiState.value = _uiState.value.copy(isLoading = false, statusText = "Ошибка сжатия")
                return@launch
            }

            processImageUseCase(
                imageBytes = bytes,
                description = "Photo via ${currentProtocol.name}",
                protocol = currentProtocol // Используем выбранный протокол
            ).collectLatest { status ->
                handleStatusUpdate(status)
            }
        }
    }

    private fun handleStatusUpdate(status: ImageStatus) {
        _uiState.value = when (status) {
            is ImageStatus.Idle -> _uiState.value.copy(isLoading = false, statusText = "Ожидание...")
            is ImageStatus.Uploading -> _uiState.value.copy(isLoading = true, statusText = "Загрузка...")
            is ImageStatus.Polling -> _uiState.value.copy(isLoading = true, statusText = "Опрос (${status.retryCount})")
            is ImageStatus.Success -> _uiState.value.copy(isLoading = false, statusText = "Готово", result = status.jsonResponse)
            is ImageStatus.Error -> _uiState.value.copy(isLoading = false, statusText = "Ошибка: ${status.message}")
        }
    }
}