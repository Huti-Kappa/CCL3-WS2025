package com.example.closetscore.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.closetscore.coroutines.GraphPoint
import com.example.closetscore.coroutines.calculateLogic
import com.example.closetscore.coroutines.calculatePrice
import com.example.closetscore.coroutines.calculateThriftAvg
import com.example.closetscore.coroutines.calculateWearFrequency
import com.example.closetscore.data.ItemRepository
import com.example.closetscore.db.ItemCategory
import com.example.closetscore.db.ItemStatus
import ir.ehsannarmani.compose_charts.extensions.format
import ir.ehsannarmani.compose_charts.models.Bars
import ir.ehsannarmani.compose_charts.models.Pie
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

    val graphPoints: StateFlow<List<GraphPoint>> = repository.items
        .map { items ->
            calculatePrice(items)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val categoryStats: StateFlow<List<Pie>> = repository.items
        .map { items ->
            val activeItems = items.filter { it.status == ItemStatus.ACTIVE }
            val total = activeItems.size.toDouble().coerceAtLeast(1.0)

            val topsCount = activeItems.count { it.category == ItemCategory.Top }
            val bottomsCount = activeItems.count { it.category == ItemCategory.Bottom }
            val shoesCount = activeItems.count { it.category == ItemCategory.Shoes }
            val outerwearCount = activeItems.count { it.category == ItemCategory.Outerwear }
            val accessoryCount = activeItems.count { it.category == ItemCategory.Accessory }
            val otherCount = activeItems.count { it.category == ItemCategory.Other }

            listOf(
                Pie(label = "Tops", data = (topsCount / total) * 100, color = Color(0xFF22C55E)),      // Grün
                Pie(label = "Bottoms", data = (bottomsCount / total) * 100, color = Color(0xFF3B82F6)),   // Blau
                Pie(label = "Shoes", data = (shoesCount / total) * 100, color = Color(0xFFEC4899)),     // Pink
                Pie(label = "Outerwear", data = (outerwearCount / total) * 100, color = Color(0xFF8B5CF6)), // Lila (Neu)
                Pie(label = "Accessory", data = (accessoryCount / total) * 100, color = Color(0xFFF97316)), // Orange (Neu)
                Pie(label = "Other", data = (otherCount / total) * 100, color = Color(0xFF9CA3AF)),     // Grau (Neu)
            ).filter { it.data > 0.0 }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    val wearStats: StateFlow<List<Bars>> = repository.items
        .map { items ->
            val buckets = calculateWearFrequency(items)

            buckets.map { bucket ->
                val color = when(bucket.label) {
                    "0" -> Color(0xFFFF9B9B) // Red
                    "1-5" -> Color(0xFFFFD750) // Yellow
                    "6-15", "16-30" -> Color(0xFF6CE590) // GreenLight
                    else -> Color(0xFF22C55E) // Green
                }

                Bars(
                    label = bucket.label,
                    values = listOf(
                        Bars.Data(value = bucket.count.toDouble(), color = SolidColor(color))
                    )
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}