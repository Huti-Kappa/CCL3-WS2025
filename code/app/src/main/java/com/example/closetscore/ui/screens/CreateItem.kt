package com.example.closetscore.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.closetscore.db.ItemCategory
import com.example.closetscore.db.ItemEntity
import com.example.closetscore.ui.AppViewModelProvider
import com.example.closetscore.ui.components.BasicInputField
import com.example.closetscore.ui.components.CategorySelection
import com.example.closetscore.ui.components.DatePickerField
import com.example.closetscore.ui.components.ImageSelector
import com.example.closetscore.ui.components.ItemCard
import com.example.closetscore.ui.components.StepperRow
import com.example.closetscore.ui.components.SwitchRow
import com.example.closetscore.ui.theme.White
import com.example.closetscore.ui.viewmodel.ItemViewModel
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ItemCreateScreen(
    itemViewModel: ItemViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateBack: () -> Unit
) {
    var isSuccess by remember { mutableStateOf(false) }
    LaunchedEffect(isSuccess) {
        if (isSuccess) {
            delay(1500)
            navigateBack()
        }
    }
    AnimatedContent(
        targetState = isSuccess,
        transitionSpec = {
            (fadeIn(animationSpec = tween(500)) + scaleIn()) togetherWith
                    fadeOut(animationSpec = tween(300))
        },
        label = "SuccessAnimation"
    ) { success ->
        if (success) {
            SuccessView()
        } else {
            AddItemGrid(itemViewModel, onSuccess = { isSuccess = true })
        }
    }
}

@Composable
fun SuccessView() {
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
            text = "Added to Closet!",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Composable
fun AddItemGrid(itemViewModel: ItemViewModel, onSuccess: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf<ItemCategory?>(null) }
    var brand by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var isSecondHand by remember { mutableStateOf(false) }
    var wearCount by remember { mutableIntStateOf(0) }
    var photoUri by remember { mutableStateOf("") }
    var store by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(16.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = "Add New Item",
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
            NameSection(
                name = name,
                onNameChange = { name = it },
                category = category,
                onCategoryChange = { category = it }
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            BasicInputField(
                label = "Item Price",
                value = price,
                onValueChange = { price = it },
                keyboardType = KeyboardType.Decimal
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            DatePickerField(
                label = "Purchase Date",
                value = date,
                onDateSelected = { date = it }
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            BasicInputField(
                label = "Brand Name",
                value = brand,
                onValueChange = { brand = it }
            )
        }
        item(span = { GridItemSpan(maxLineSpan) }) {
            BasicInputField(
                label = "Store",
                value = store,
                onValueChange = { store = it }
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
                label = "Thrifted?",
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

                    val finalDate = if (date.isNotBlank()) {
                        try {
                            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            LocalDate.parse(date, formatter)
                        } catch (e: Exception) {
                            LocalDate.now()
                        }
                    } else {
                        LocalDate.now()
                    }

                    if (category != null) {
                        val newItem = ItemEntity(
                            name = name,
                            category = category!!,
                            price = finalPrice,
                            date = finalDate,
                            brand = brand,
                            store = store,
                            isSecondHand = isSecondHand,
                            wearCount = wearCount,
                            photoUri = photoUri.ifBlank { null }
                        )
                        itemViewModel.addItem(newItem)
                        onSuccess()
                    }
                }
            ) {
                Text("Add to Closet")
            }
        }
    }
}

@Composable
fun NameSection(
    name: String,
    onNameChange: (String) -> Unit,
    category: ItemCategory?,
    onCategoryChange: (ItemCategory?) -> Unit
) {
    Column(
        modifier = Modifier
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(16.dp)
            )
            .background(
                color = White,
                shape = RoundedCornerShape(16.dp),

            )
            .padding(12.dp)
    ) {
        BasicInputField(
            label = "Item Name",
            value = name,
            onValueChange = onNameChange
        )
        CategorySelection(
            label = "Category",
            selectedCategory = category,
            onCategorySelected = onCategoryChange
        )
    }
}