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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.closetscore.db.ItemCategory
import com.example.closetscore.db.ItemEntity
import com.example.closetscore.ui.AppViewModelProvider
import com.example.closetscore.ui.components.BasicInputField
import com.example.closetscore.ui.components.CategorySelection
import com.example.closetscore.ui.components.ImageSelector
import com.example.closetscore.ui.components.StepperRow
import com.example.closetscore.ui.components.SwitchRow
import com.example.closetscore.ui.viewmodel.ItemViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ItemCreateScreen(
    itemViewModel: ItemViewModel = viewModel(factory = AppViewModelProvider.Factory),
    navigateBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var category by remember { mutableStateOf<ItemCategory?>(null) }
    var isSecondHand by remember { mutableStateOf(false) }
    var wearCount by remember { mutableIntStateOf(0) }
    var photoUri by remember { mutableStateOf("") }


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
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Add New Item",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.padding(bottom = 24.dp)
                )
                ImageSelector(
                    onImageSelected = { newUri -> photoUri = newUri }
                )
                BasicInputField(
                    label = "Item Name",
                    value = name,
                    onValueChange = { name = it }
                )
                BasicInputField(
                    label = "Brand Name",
                    value = brand,
                    onValueChange = { brand = it }
                )
                BasicInputField(
                    label = "Item Price",
                    value = price,
                    onValueChange = { price = it },
                    keyboardType = KeyboardType.Decimal
                )

                CategorySelection(
                    label = "Category",
                    selectedCategory = category,
                    onCategorySelected = { newCategory -> category = newCategory }
                )

                StepperRow(
                    label = "Times Worn",
                    value = wearCount,
                    onValueChange = { wearCount = it }
                )

                SwitchRow(
                    label = "Thrifted?",
                    checked = isSecondHand,
                    onCheckedChange = { isSecondHand = it }
                )

                Button(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    enabled = name.isNotBlank() && category != null,
                    onClick = {
                        val finalPrice = price.toDoubleOrNull() ?: 0.0
                        if (category != null) {
                            val newItem = ItemEntity(
                                name = name,
                                brand = brand,
                                category = category!!,
                                price = finalPrice,
                                isSecondHand = isSecondHand,
                                wearCount = wearCount,
                                photoUri = if (photoUri.isBlank()) null else photoUri
                            )
                            itemViewModel.addItem(newItem)
                            isSuccess = true
                        }
                    }
                ) {
                    Text("Save Item")
                }
            }
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