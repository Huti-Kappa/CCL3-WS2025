package com.example.closetscore.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.closetscore.coroutines.calculateLogic
import com.example.closetscore.coroutines.calculateThriftAvg
import com.example.closetscore.data.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class statsEntryUiState(
    val itemSize: Int = 0,
    val totalWears: Int = 0,
    val thriftAverage: Double = 0.0,
    val priceAverage: Double = 0.0,
)

class ScoreViewModel(repository: ItemRepository) : ViewModel() {
    val score: StateFlow<Int> = repository.items
        .map { items ->
            calculateLogic(items)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val dataState: StateFlow<statsEntryUiState> = repository.items
        .map { items ->
            statsEntryUiState(
                itemSize = items.size,
                totalWears = items.sumOf { it.wearCount },
                thriftAverage = calculateThriftAvg(items),
                priceAverage = items.map { it.price }.average()
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = statsEntryUiState()
        )
}