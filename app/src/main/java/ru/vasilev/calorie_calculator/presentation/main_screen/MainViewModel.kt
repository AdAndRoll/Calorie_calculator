package ru.vasilev.calorie_calculator.presentation.main_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.vasilev.domain.model.ProtocolType
import ru.vasilev.domain.usecase.ProcessImageUseCase
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val processImageUseCase: ProcessImageUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun onImageSelected(bytes: ByteArray) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, statusText = "Обработка...")

            processImageUseCase(
                imageBytes = bytes,
                description = "Photo from App",
                protocol = ProtocolType.REST
            ).collectLatest { result ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    statusText = "Готово!",
                    result = result.toString()
                )
            }
        }
    }
}