package com.example.closetscore.data
import com.example.closetscore.db.ItemCategory
import com.example.closetscore.db.ItemStatus
import java.time.LocalDate

data class Item(
    val id: Int = 0,
    val name: String,
    val photoUri: String?,
    val brand: String?,
    val category: ItemCategory,
    val price: Double,
    val isSecondHand: Boolean,
    val wearCount: Int = 0,
    val store: String?,
    val date: LocalDate,
    val status: ItemStatus = ItemStatus.ACTIVE
)