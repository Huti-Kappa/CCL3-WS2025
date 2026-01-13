package com.example.closetscore.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.closetscore.data.TemplateRepository
import com.example.closetscore.db.ItemStatus
import com.example.closetscore.db.TemplateEntity
import com.example.closetscore.db.TemplateWithItems
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class TemplateViewModel(
    private val templateRepository: TemplateRepository
) : ViewModel() {

    val templatesWithItems: StateFlow<List<TemplateWithItems>> =
        templateRepository.templatesWithItems.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )


    fun getTemplateWithItems(templateId: Int){
        viewModelScope.launch{
            templateRepository.getTemplateWithItems(templateId)
        }
    }

    fun createTemplate(
        name: String,
        itemIds: List<Int>,
        onSuccess: (Int) -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                val templateId = templateRepository.addTemplate(
                    TemplateEntity(
                        name = name,
                        date = LocalDate.now(),
                        status = ItemStatus.ACTIVE
                    )
                )

                itemIds.forEach { itemId ->
                    templateRepository.addItemToTemplate(templateId.toInt(), itemId)
                }

                onSuccess(templateId.toInt())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteTemplate(templateId: Int, templateEntity: TemplateEntity) {
        viewModelScope.launch {
            try {
                templateRepository.deleteTemplate(templateEntity)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addItemToTemplate(templateId: Int, itemId: Int) {
        viewModelScope.launch {
            try {
                templateRepository.addItemToTemplate(templateId, itemId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun removeItemFromTemplate(templateId: Int, itemId: Int) {
        viewModelScope.launch {
            try {
                templateRepository.removeItemFromTemplate(templateId, itemId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}