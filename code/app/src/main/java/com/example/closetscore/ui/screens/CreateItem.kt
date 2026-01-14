package com.example.closetscore.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.closetscore.data.Item
import com.example.closetscore.db.BrandType
import com.example.closetscore.db.ItemCategory
import com.example.closetscore.db.MaterialType
import com.example.closetscore.ui.AppViewModelProvider
import com.example.closetscore.ui.components.*
import com.example.closetscore.ui.theme.*
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
            tint = Green, // Uses your theme Green
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
    var brandName by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var isSecondHand by remember { mutableStateOf(false) }
    var wearCount by remember { mutableIntStateOf(0) }
    var photoUri by remember { mutableStateOf("") }
    var dateString by remember { mutableStateOf("") }
    var brandType by remember { mutableStateOf(BrandType.STANDARD) }
    var material by remember { mutableStateOf(MaterialType.MIXED) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Add New Item",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            HorizontalDivider(thickness = 1.dp, color = DarkGrey)
        }

        AddImage(
            photoUri = photoUri,
            onUriChange = { newUri -> photoUri = newUri }
        )

        NameSection(
            name = name,
            onNameChange = { name = it },
            category = category,
            onCategoryChange = { category = it }
        )

        PurchaseSection(
            price = price,
            onPriceChange = { price = it },
            date = dateString,
            onDateSelected = { dateString = it },
            brandName = brandName,
            onBrandChange = { brandName = it }
        )

        ScoringSection(
            brandType = brandType,
            onBrandTypeChange = { brandType = it },
            material = material,
            onMaterialChange = { material = it },
            isSecondHand = isSecondHand,
            onSecondHandChange = { isSecondHand = it }
        )

        TimesWornSection(
            wearCount = wearCount,
            onWearChange = { wearCount = it }
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 32.dp),
            enabled = name.isNotBlank() && category != null,
            colors = ButtonDefaults.buttonColors(containerColor = Red),
            onClick = {
                val finalPrice = price.toDoubleOrNull() ?: 0.0

                val finalDate = if (dateString.isNotBlank()) {
                    try {
                        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                        LocalDate.parse(dateString, formatter)
                    } catch (e: Exception) {
                        LocalDate.now()
                    }
                } else {
                    LocalDate.now()
                }

                if (category != null) {
                    val newItem = Item(
                        name = name,
                        category = category!!,
                        price = finalPrice,
                        dateAcquired = finalDate,
                        brandName = brandName.ifBlank { null },
                        brandType = brandType,
                        material = material,
                        isSecondHand = isSecondHand,
                        wearCount = wearCount,
                        photoUri = photoUri.ifBlank { null }
                    )

                    itemViewModel.addItem(newItem)
                    onSuccess()
                }
            }
        ) {
            Text("Add to Closet", modifier = Modifier.padding(8.dp))
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
    SectionContainer {
        BasicInputField(
            label = "Item Name",
            value = name,
            onValueChange = onNameChange
        )
        LabelText("Category")
        val categoryOptions = ItemCategory.entries.map { it.name.replace("_", " ") }
        SegmentedEnumSelector(
            options = categoryOptions,
            selectedOptionIndex = category?.ordinal ?: -1,
            onOptionSelected = { index ->
                onCategoryChange(ItemCategory.entries[index])
            }
        )

    }
}

@Composable
fun PurchaseSection(
    price: String,
    onPriceChange: (String) -> Unit,
    date: String,
    onDateSelected: (String) -> Unit,
    brandName: String,
    onBrandChange: (String) -> Unit
) {
    SectionContainer {
        PriceInputField(
            label = "Item Price",
            value = price,
            onValueChange = onPriceChange,
        )
        DatePickerField(
            label = "Purchase Date",
            value = date,
            onDateSelected = onDateSelected
        )
        BasicInputField(
            label = "Brand (Optional)",
            value = brandName,
            onValueChange = onBrandChange
        )
    }
}
@Composable
fun ScoringSection(
    brandType: BrandType,
    onBrandTypeChange: (BrandType) -> Unit,
    material: MaterialType,
    onMaterialChange: (MaterialType) -> Unit,
    isSecondHand: Boolean,
    onSecondHandChange: (Boolean) -> Unit
) {
    SectionContainer {
        LabelText("Brand Type")

        val brandOptions = BrandType.entries.map {
            when(it) {
                BrandType.ECO_SUSTAINABLE -> "Eco / Ethical"
                BrandType.STANDARD -> "Standard"
                BrandType.FAST_FASHION -> "Fast Fashion"
            }
        }

        SegmentedEnumSelector(
            options = brandOptions,
            selectedOptionIndex = brandType.ordinal,
            onOptionSelected = { index ->
                onBrandTypeChange(BrandType.entries[index])
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        LabelText("Material")

        val materialOptions = MaterialType.entries.map {
            it.name.lowercase().replaceFirstChar { char -> char.uppercase() }
        }

        SegmentedEnumSelector(
            options = materialOptions,
            selectedOptionIndex = material.ordinal,
            onOptionSelected = { index ->
                onMaterialChange(MaterialType.entries[index])
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        SwitchRow(
            label = "Thrifted / Second-hand",
            checked = isSecondHand,
            onCheckedChange = onSecondHandChange
        )
    }
}

@Composable
fun TimesWornSection(wearCount: Int, onWearChange: (Int) -> Unit) {
    SectionContainer {
        StepperRow(
            label = "Times Worn",
            value = wearCount,
            onValueChange = onWearChange
        )
    }
}