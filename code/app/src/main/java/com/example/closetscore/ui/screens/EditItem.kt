package com.example.closetscore.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.closetscore.db.BrandType
import com.example.closetscore.db.ItemCategory
import com.example.closetscore.db.ItemEntity
import com.example.closetscore.db.MaterialType
import com.example.closetscore.ui.AppViewModelProvider
import com.example.closetscore.ui.components.BasicInputField
import com.example.closetscore.ui.components.CategorySelection
import com.example.closetscore.ui.components.DatePickerField
import com.example.closetscore.ui.components.ImageSelector
import com.example.closetscore.ui.components.StepperRow
import com.example.closetscore.ui.components.SwitchRow
import com.example.closetscore.ui.viewmodel.ItemViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun EditItemScreen(
    itemId: Int,
    navigateBack: () -> Unit,
    itemViewModel: ItemViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    var originalItem by remember { mutableStateOf<ItemEntity?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    var name by remember { mutableStateOf("") }
    var brandName by remember { mutableStateOf("") } // Renamed from brand
    var brandType by remember { mutableStateOf(BrandType.STANDARD) } // New
    var material by remember { mutableStateOf(MaterialType.MIXED) } // New
    var price by remember { mutableStateOf("") }
    var category by remember { mutableStateOf<ItemCategory?>(null) }
    var isSecondHand by remember { mutableStateOf(false) }
    var wearCount by remember { mutableIntStateOf(0) }
    var photoUri by remember { mutableStateOf("") }
    var dateString by remember { mutableStateOf("") }
    var isSuccess by remember { mutableStateOf(false) }
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    LaunchedEffect(itemId) {
        withContext(Dispatchers.IO) {
            val loadedItem = itemViewModel.repository.getItemById(itemId)
            originalItem = loadedItem

            name = loadedItem.name
            brandName = loadedItem.brandName ?: ""
            brandType = loadedItem.brandType
            material = loadedItem.material
            price = loadedItem.price.toString()
            category = loadedItem.category
            isSecondHand = loadedItem.isSecondHand
            wearCount = loadedItem.wearCount
            photoUri = loadedItem.photoUri ?: ""
            dateString = loadedItem.dateAcquired.format(dateFormatter)

            isLoading = false
        }
    }

    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            delay(1500)
            navigateBack()
        }
    }

    if (isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Loading...")
        }
    } else {
        AnimatedContent(
            targetState = isSuccess,
            transitionSpec = {
                (fadeIn(animationSpec = tween(500)) + scaleIn()) togetherWith
                        fadeOut(animationSpec = tween(300))
            },
            label = "SuccessAnimation"
        ) { success ->
            if (success) {
                EditSuccessView()
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 150.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Text(
                            text = "Edit Item",
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )
                    }

                    item(span = { GridItemSpan(maxLineSpan) }) {
                        ImageSelector(
                            onImageSelected = { newUri -> photoUri = newUri }
                        )
                    }

                    item(span = { GridItemSpan(maxLineSpan) }) {
                        BasicInputField(
                            label = "Item Name",
                            value = name,
                            onValueChange = { name = it }
                        )
                    }

                    item(span = { GridItemSpan(maxLineSpan) }) {
                        CategorySelection(
                            label = "Category",
                            selectedCategory = category,
                            onCategorySelected = { newCategory -> category = newCategory }
                        )
                    }

                    item {
                        BasicInputField(
                            label = "Price",
                            value = price,
                            onValueChange = { price = it },
                            keyboardType = KeyboardType.Decimal
                        )
                    }

                    item {
                        DatePickerField(
                            label = "Date Acquired",
                            value = dateString,
                            onDateSelected = { dateString = it }
                        )
                    }


                    item {
                        BasicInputField(
                            label = "Brand Name",
                            value = brandName,
                            onValueChange = { brandName = it }
                        )
                    }

                    item {
                        EnumSelectionRow(
                            label = "Brand Type",
                            currentSelection = brandType,
                            onSelectionChanged = { brandType = it },
                            options = BrandType.values().toList(),
                            nameProvider = { it.name.replace("_", " ") }
                        )
                    }

                    item(span = { GridItemSpan(maxLineSpan) }) {
                        EnumSelectionRow(
                            label = "Material",
                            currentSelection = material,
                            onSelectionChanged = { material = it },
                            options = MaterialType.values().toList(),
                            nameProvider = { it.name }
                        )
                    }

                    item(span = { GridItemSpan(maxLineSpan) }) {
                        StepperRow(
                            label = "Times Worn",
                            value = wearCount,
                            onValueChange = { wearCount = it }
                        )
                    }

                    item(span = { GridItemSpan(maxLineSpan) }) {
                        SwitchRow(
                            label = "Thrifted / Second Hand?",
                            checked = isSecondHand,
                            onCheckedChange = { isSecondHand = it }
                        )
                    }

                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Button(
                            modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                            enabled = name.isNotBlank() && category != null,
                            onClick = {
                                val finalPrice = price.toDoubleOrNull() ?: 0.0

                                val finalDate = try {
                                    if (dateString.isNotBlank()) {
                                        LocalDate.parse(dateString, dateFormatter)
                                    } else LocalDate.now()
                                } catch (e: DateTimeParseException) {
                                    LocalDate.now()
                                }

                                if (category != null && originalItem != null) {
                                    val updatedItem = ItemEntity(
                                        id = itemId,
                                        name = name,
                                        category = category!!,
                                        price = finalPrice,
                                        dateAcquired = finalDate,
                                        brandName = brandName.ifBlank { null },
                                        brandType = brandType,
                                        material = material,
                                        isSecondHand = isSecondHand,
                                        wearCount = wearCount,
                                        photoUri = photoUri.ifBlank { null },
                                        status = originalItem!!.status
                                    )
                                    itemViewModel.updateItem(updatedItem)
                                    isSuccess = true
                                }
                            }
                        ) {
                            Text("Save Changes")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditSuccessView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Success",
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(120.dp)
        )
        Text(
            text = "Item Updated!",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> EnumSelectionRow(
    label: String,
    currentSelection: T,
    onSelectionChanged: (T) -> Unit,
    options: List<T>,
    nameProvider: (T) -> String
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                FilterChip(
                    selected = (option == currentSelection),
                    onClick = { onSelectionChanged(option) },
                    label = {
                        Text(
                            text = nameProvider(option),
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}