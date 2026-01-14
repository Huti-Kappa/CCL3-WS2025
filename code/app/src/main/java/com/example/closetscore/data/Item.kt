package com.example.closetscore.data

import com.example.closetscore.db.BrandType
import com.example.closetscore.db.ItemCategory
import com.example.closetscore.db.ItemStatus
import com.example.closetscore.db.MaterialType
import java.time.LocalDate

data class Item(
    val id: Int = 0,
    val name: String,
    val photoUri: String?,
    val brandName: String?,
    val brandType: BrandType,
    val material: MaterialType,
    val category: ItemCategory,
    val price: Double,
    val isSecondHand: Boolean,
    val wearCount: Int = 0,
    val dateAcquired: LocalDate,
    val status: ItemStatus = ItemStatus.ACTIVE
)