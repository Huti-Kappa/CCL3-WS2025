package com.example.closetscore.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.closetscore.data.Item
import com.example.closetscore.db.BrandType
import com.example.closetscore.db.ItemCategory
import com.example.closetscore.db.ItemEntity
import com.example.closetscore.db.MaterialType
import com.example.closetscore.ui.viewmodel.ItemViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun AddItemGrid(
    itemViewModel: ItemViewModel,
    onSuccess: () -> Unit,
    navigateBack: () -> Unit,
    isEditMode: Boolean = false
){
    val state by itemViewModel.formState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                IconButton(
                    onClick = navigateBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Text(
                    if (isEditMode) "Edit Item" else "Add Item",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            AddImage(
                photoUri = state.photoUri,
                onUriChange = itemViewModel::updatePhotoUri
            )

            NameSection(
                name = state.name,
                onNameChange = itemViewModel::updateName,
                category = state.category,
                onCategoryChange = itemViewModel::updateCategory,
            )

            PurchaseSection(
                price = state.price,
                onPriceChange = itemViewModel::updatePrice,
                date = state.dateString,
                onDateSelected = itemViewModel::updateDate,
                brandName = state.brandName,
                onBrandChange = itemViewModel::updateBrandName,
            )

            ScoringSection(
                brandType = state.brandType,
                onBrandTypeChange = itemViewModel::updateBrandType,
                material = state.material,
                onMaterialChange = itemViewModel::updateMaterial,
                isSecondHand = state.isSecondHand,
                onSecondHandChange = itemViewModel::updateIsSecondHand,
            )

            TimesWornSection(
                wearCount = state.wearCount,
                onWearChange = itemViewModel::updateWearCount,
            )

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 32.dp),
                // HIER: Prüfung auf Name, Category (Type), Price und Date
                enabled = state.name.isNotBlank() &&
                        state.category != null &&
                        state.price.isNotBlank() &&
                        state.dateString.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                onClick = {
                    val finalPrice = state.price.toDoubleOrNull() ?: 0.0

                    val finalDate = if (state.dateString.isNotBlank()) {
                        try {
                            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                            LocalDate.parse(state.dateString, formatter)
                        } catch (e: Exception) {
                            LocalDate.now()
                        }
                    } else {
                        LocalDate.now()
                    }

                    if (state.category != null) {
                        if (isEditMode) {
                            val updatedItem = ItemEntity(
                                id = state.id,
                                name = state.name,
                                category = state.category!!,
                                price = finalPrice,
                                dateAcquired = finalDate,
                                brandName = state.brandName.ifBlank { null },
                                brandType = state.brandType,
                                material = state.material,
                                isSecondHand = state.isSecondHand,
                                wearCount = state.wearCount,
                                photoUri = state.photoUri.ifBlank { null }
                            )
                            itemViewModel.updateItem(updatedItem)
                        } else {
                            val newItem = Item(
                                name = state.name,
                                category = state.category!!,
                                price = finalPrice,
                                dateAcquired = finalDate,
                                brandName = state.brandName.ifBlank { null },
                                brandType = state.brandType,
                                material = state.material,
                                isSecondHand = state.isSecondHand,
                                wearCount = state.wearCount,
                                photoUri = state.photoUri.ifBlank { null }
                            )
                            itemViewModel.addItem(newItem)
                        }
                        onSuccess()
                    }
                }
            ) {
                Text(
                    if (isEditMode) "Save Changes" else "Add to Closet",
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun NameSection(
    name: String,
    onNameChange: (String) -> Unit,
    category: ItemCategory?,
    onCategoryChange: (ItemCategory) -> Unit
) {
    SectionContainer {
        BasicInputField(
            label = "Item Name*",
            value = name,
            onValueChange = onNameChange
        )
        LabelText("Category*")
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
            label = "Item Price*",
            value = price,
            onValueChange = onPriceChange,
        )
        DatePickerField(
            label = "Purchase Date*", // HIER: Sternchen hinzugefügt
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