package ru.vasilev.calorie_calculator.presentation.main_screen

import ru.vasilev.domain.model.ProtocolType

data class MainUiState(
    val statusText: String = "Выберите фото для анализа",
    val isLoading: Boolean = false,
    val result: String? = null,
    val selectedProtocol: ProtocolType = ProtocolType.REST // Добавили поле
)