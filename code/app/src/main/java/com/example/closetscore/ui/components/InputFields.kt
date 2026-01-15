package com.example.closetscore.ui.components

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Checkroom
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.closetscore.db.BrandType
import com.example.closetscore.db.ItemCategory
import com.example.closetscore.ui.theme.Black
import com.example.closetscore.ui.theme.DarkGrey
import com.example.closetscore.ui.theme.DarkestGrey
import com.example.closetscore.ui.theme.Grey
import com.example.closetscore.ui.theme.LightGreen
import com.example.closetscore.ui.theme.White
import com.example.closetscore.ui.viewmodel.ItemViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.Int


@Composable
fun BasicInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    prefix: String? = null
) {
    Column {
        Text(
            text = label,
            color = Black
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = label,
                    color = DarkGrey
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            prefix = if (prefix != null) { { Text(prefix) } } else null,
            singleLine = true,
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Grey,
                focusedContainerColor = Grey,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )
    }
}

@Composable
fun DatePickerField(
    label: String,
    value: String,
    onDateSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column {
            Text(
                text = label,
                color = Black
            )
            TextField(
                value = value,
                onValueChange = { },
                readOnly = true,
                placeholder = { Text("Select a date") },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Grey,
                    focusedContainerColor = Grey,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    val calendar = Calendar.getInstance()
                    val datePickerDialog = DatePickerDialog(
                        context,
                        { _, year: Int, month: Int, dayOfMonth: Int ->
                            val selectedDate = Calendar.getInstance()
                            selectedDate.set(year, month, dayOfMonth)
                            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                            val formattedDate = dateFormat.format(selectedDate.time)
                            onDateSelected(formattedDate)
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    )
                    datePickerDialog.show()
                }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategorySelection(
    label: String,
    selectedCategory: ItemCategory?,
    onCategorySelected: (ItemCategory) -> Unit
) {
    var showSheet by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column {
            Text(
                text = label,
                color = Black
            )
            TextField(
                value = selectedCategory?.name ?: "",
                onValueChange = {},
                readOnly = true,
                placeholder = {
                    Text(
                        text = "Select a category",
                        color = DarkGrey
                    )
                },
                trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Grey,
                    focusedContainerColor = Grey,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    showSheet = true
                }
        )
    }

    if (showSheet) {
        val categories = remember { ItemCategory.values() }
        ModalBottomSheet(
            onDismissRequest = { showSheet = false }
        ) {
            Text(
                text = "Select Category",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(16.dp)
            )
            LazyColumn {
                items(categories) { categoryItem ->
                    ListItem(
                        headlineContent = { Text(categoryItem.name) },
                        leadingContent = {
                            Icon(Icons.Default.Checkroom, contentDescription = null)
                        },
                        modifier = Modifier.clickable {
                            onCategorySelected(categoryItem)
                            showSheet = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun StepperRow(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilledTonalIconButton(
                onClick = { if (value > 0) onValueChange(value - 1) },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.Remove, contentDescription = "Decrease")
            }


            OutlinedTextField(
                value = if (value == 0) "" else value.toString(),
                onValueChange = { input ->
                    if (input.all { it.isDigit() }) {
                        onValueChange(input.toIntOrNull() ?: 0)
                    }
                },
                placeholder = { Text("0") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(80.dp),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center)
            )

            FilledTonalIconButton(
                onClick = { onValueChange(value + 1) },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Increase")
            }
        }
    }
}

@Composable
fun PriceInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "Price",
    currencySymbol: String = "€"
) {
    BasicInputField(
        label = label,
        value = value,
        onValueChange = { input ->
            if (input.isEmpty() || input.matches(Regex("^\\d*\\.?\\d*$"))) {
                onValueChange(input)
            }
        },
        keyboardType = KeyboardType.Decimal,
        prefix = currencySymbol
    )
}
@Composable
fun SegmentedEnumSelector(
    options: List<String>,
    selectedOptionIndex: Int,
    onOptionSelected: (Int) -> Unit
) {
    val selectedColor = LightGreen
    val unselectedColor = Grey
    val selectedTextColor = White
    val unselectedTextColor = DarkestGrey

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        val chunkedOptions = options.chunked(3)

        var globalIndex = 0

        chunkedOptions.forEach { rowOptions ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowOptions.forEach { text ->
                    val currentIndex = globalIndex
                    val isSelected = currentIndex == selectedOptionIndex

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) selectedColor else unselectedColor)
                            .clickable { onOptionSelected(currentIndex) }
                    ) {
                        Text(
                            text = text,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isSelected) selectedTextColor else unselectedTextColor
                        )
                    }
                    globalIndex++
                }
                if (rowOptions.size < 3) {
                    repeat(3 - rowOptions.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun LabelText(text: String) {
    Text(
        text = text,
        color = Black,
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
fun SectionContainer(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
            .background(color = White, shape = RoundedCornerShape(16.dp))
            .border(width = 1.dp, color = Grey, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        content()
    }
}
