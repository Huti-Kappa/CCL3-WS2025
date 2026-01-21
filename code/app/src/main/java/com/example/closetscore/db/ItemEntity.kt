package com.example.closetscore.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "items")
data class ItemEntity(
    @PrimaryKey(autoGenerate = true)
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

enum class BrandType {
    ECO_SUSTAINABLE, // + Score (e.g. Patagonia, Local Handmade)
    STANDARD,        // Neutral (e.g. Nike, Levis, Dept Store)
    FAST_FASHION     // - Score (e.g. Shein, H&M)
}

enum class MaterialType {
    NATURAL,    // + Score (Cotton, Wool, Linen - Biodegradable)
    SYNTHETIC,  // - Score (Polyester, Nylon - Microplastics)
    MIXED       // Neutral (Hard to recycle)
}

enum class ItemCategory {
    Top,
    Bottom,
    Shoes,
    Outerwear,
    Other,
    Accessory
}

enum class ItemStatus {
    ACTIVE,
    SOLD,
    DONATED,
    TRASHED,
    LOST,
}