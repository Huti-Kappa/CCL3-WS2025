package com.example.closetscore.ui.viewmodel

import android.app.appsearch.SearchResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.closetscore.data.Item
import com.example.closetscore.data.ItemRepository
import com.example.closetscore.db.BrandType
import com.example.closetscore.db.ItemCategory
import com.example.closetscore.db.ItemEntity
import com.example.closetscore.db.MaterialType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter


data class ItemEntryUiState(
    val id: Int = 0,
    val name: String = "",
    val category: ItemCategory? = null,
    val brandName: String = "",
    val price: String = "",
    val dateString: String = "",
    val isSecondHand: Boolean = false,
    val wearCount: Int = 0,
    val photoUri: String = "",
    val brandType: BrandType = BrandType.STANDARD,
    val material: MaterialType = MaterialType.MIXED
)

data class ItemUiState(
    val items: Flow<List<ItemEntity>>
)

class ItemViewModel (val repository: ItemRepository) : ViewModel() {

    val itemUiState = repository.items.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        emptyList()

    )
    private val _searchResults = MutableStateFlow<List<Item>>(emptyList())
    val searchResults = _searchResults.asStateFlow()


    fun searchItems(query: String) {
        if (query.isBlank()) {
            _searchResults.value = emptyList()
        } else {
            _searchResults.value = itemUiState.value.filter { item ->
                item.name.contains(query, ignoreCase = true)
            }
        }
    }
    private val _formState = MutableStateFlow(ItemEntryUiState())
    val formState = _formState.asStateFlow()
    private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    fun initializeForm(itemId: Int?) {
        if (itemId != null && itemId != 0) {
            viewModelScope.launch {
                val item = repository.getItemById(itemId)
                item?.let {
                    _formState.value = ItemEntryUiState(
                        id = it.id,
                        name = it.name,
                        category = it.category,
                        brandName = it.brandName ?: "",
                        price = it.price.toString(),
                        dateString = it.dateAcquired.format(dateFormatter),
                        isSecondHand = it.isSecondHand,
                        wearCount = it.wearCount,
                        photoUri = it.photoUri ?: "",
                        brandType = it.brandType,
                        material = it.material
                    )
                }
            }
        } else {
            _formState.value = ItemEntryUiState()
        }
    }
    fun updateName(name: String) = _formState.update { it.copy(name = name) }
    fun updateCategory(category: ItemCategory) = _formState.update { it.copy(category = category) }
    fun updateBrandName(brand: String) = _formState.update { it.copy(brandName = brand) }
    fun updatePrice(price: String) = _formState.update { it.copy(price = price) }
    fun updateDate(date: String) = _formState.update { it.copy(dateString = date) }
    fun updateIsSecondHand(isSecond: Boolean) = _formState.update { it.copy(isSecondHand = isSecond) }
    fun updateWearCount(count: Int) = _formState.update { it.copy(wearCount = count.coerceAtLeast(0)) }
    fun updatePhotoUri(uri: String) = _formState.update { it.copy(photoUri = uri) }
    fun updateBrandType(type: BrandType) = _formState.update { it.copy(brandType = type) }
    fun updateMaterial(mat: MaterialType) = _formState.update { it.copy(material = mat) }

    fun getItemById(itemId: Int){
        viewModelScope.launch{
            repository.getItemById(itemId)
        }
    }

    fun addItem(item: Item) {
        viewModelScope.launch {
            repository.addItem(item)
        }
    }

    fun updateItem(itemEntity: ItemEntity){
        viewModelScope.launch{
            repository.updateItem(itemEntity)
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