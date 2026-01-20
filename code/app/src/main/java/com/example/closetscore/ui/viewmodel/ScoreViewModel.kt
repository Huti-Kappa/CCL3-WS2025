package com.example.closetscore.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.closetscore.coroutines.calculateLogic
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

}
