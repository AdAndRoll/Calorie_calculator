package ru.vasilev.calorie_calculator.presentation.main_screen

data class MainUiState(
    val statusText: String = "Выберите фото для анализа",
    val isLoading: Boolean = false,
    val result: String? = null
)