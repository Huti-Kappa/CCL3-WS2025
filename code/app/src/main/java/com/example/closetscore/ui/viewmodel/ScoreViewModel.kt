package com.example.closetscore.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.closetscore.data.Item
import com.example.closetscore.data.ItemRepository
import com.example.closetscore.db.BrandType
import com.example.closetscore.db.ItemStatus
import com.example.closetscore.db.MaterialType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlin.math.min

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

    private fun calculateLogic(items: List<Item>): Int {
        var score = 0
        if(items.isEmpty()){
            return 0
        }
        items.forEach { item ->
            score += calculateValue(item)
        }
        val average = score / items.size
        return average.coerceIn(0, 100)
    }
    fun calculateValue(item: Item): Int {
        var score = 50
        if (item.isSecondHand) {
            score += 20
        }

        when (item.brandType) {
            BrandType.FAST_FASHION -> score -= 20
            BrandType.ECO_SUSTAINABLE -> score += 15
            BrandType.STANDARD -> score += 0
        }

        when (item.material) {
            MaterialType.NATURAL -> score += 10
            MaterialType.SYNTHETIC -> score -= 10
            MaterialType.MIXED -> score -= 5
        }

        score += item.wearCount/3

        when (item.status) {
            ItemStatus.TRASHED -> {
                if (item.wearCount > 50) {
                    score += 0
                } else if (item.wearCount > 30) {
                    score -= 10
                } else {
                    score -= 50
                }
            }
            ItemStatus.SOLD, ItemStatus.DONATED -> score += 10
            ItemStatus.ACTIVE -> score+=0
            ItemStatus.LOST -> score-=1
        }
        return score
    }
}

