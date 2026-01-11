package com.example.closetscore.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.closetscore.data.ItemRepository
import com.example.closetscore.db.ItemCategory
import com.example.closetscore.db.ItemEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ItemUiState(
    val items: Flow<List<ItemEntity>>
)
class ItemViewModel (val repository: ItemRepository) : ViewModel() {

    val itemUiState = repository.items.stateIn(
        viewModelScope,
        // SharingStarted.WhileSubscribed(5000) controls when the flow starts and stops collecting. 5 sec avoids restarting the flow immediately if a UI rotates or briefly detaches.
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    fun addItem(itemEntity: ItemEntity) {
        viewModelScope.launch{
            repository.addItem(itemEntity)
        }
    }

    fun incrementWearCount(itemId: Int){
        viewModelScope.launch{
            repository.incrementWearCount(itemId)
        }
    }

    fun deleteItem(itemEntity: ItemEntity){
        viewModelScope.launch{
            repository.deleteItem(itemEntity)
        }
    }

}