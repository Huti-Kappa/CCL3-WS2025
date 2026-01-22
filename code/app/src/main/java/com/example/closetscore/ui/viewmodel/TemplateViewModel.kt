package com.example.closetscore.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.closetscore.data.ItemRepository
import com.example.closetscore.data.TemplateRepository
import com.example.closetscore.db.ItemEntity
import com.example.closetscore.db.ItemStatus
import com.example.closetscore.db.TemplateEntity
import com.example.closetscore.db.TemplateWithItems
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class TemplateViewModel(
    val repository: TemplateRepository,
    private val itemRepository: ItemRepository
) : ViewModel() {

    val templatesWithItems: StateFlow<List<TemplateWithItems>> =
        repository.templatesWithItems.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createTemplate(
        name: String,
        itemIds: List<Int>,
        onSuccess: (Int) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val templateId = repository.addTemplate(
                    TemplateEntity(
                        name = name,
                        date = LocalDate.now(),
                        status = ItemStatus.ACTIVE
                    )
                )

                itemIds.forEach { itemId ->
                    repository.addItemToTemplate(templateId.toInt(), itemId)
                }

                onSuccess(templateId.toInt())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteTemplate(templateId: Int) {
        viewModelScope.launch {
            try {
                repository.deleteTemplate(templateId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun incrementTemplateWearCount(templateId: Int) {
        viewModelScope.launch {
            try {
                repository.incrementWearCount(templateId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateTemplateWearCount(templateEntity: TemplateEntity) {
        viewModelScope.launch {
            try {
                repository.updateTemplateWearCount(templateEntity)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateTemplateAndItems(templateEntity: TemplateEntity, items: List<ItemEntity>) {
        viewModelScope.launch {
            try {
                repository.updateTemplate(templateEntity)
                itemRepository.updateItems(items)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun addItemToTemplate(templateId: Int, itemId: Int) {
        viewModelScope.launch {
            try {
                repository.addItemToTemplate(templateId, itemId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateTemplate(templateEntity: TemplateEntity) {
        viewModelScope.launch {
            try {
                repository.updateTemplate(templateEntity)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun removeItemFromTemplate(templateId: Int, itemId: Int) {
        viewModelScope.launch {
            try {
                repository.removeItemFromTemplate(templateId, itemId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}