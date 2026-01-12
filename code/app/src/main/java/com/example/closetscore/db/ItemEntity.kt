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
    val brand: String?,
    val category: ItemCategory,
    val price: Double,
    val isSecondHand: Boolean,
    val wearCount: Int = 0,
    val store: String?,
    val date: LocalDate,
    val status: ItemStatus = ItemStatus.ACTIVE
)

enum class ItemCategory {
    Top,        // T-shirts, Shirts, Blouses, Sweaters
    Bottom,     // Jeans, Skirts, Shorts, Trousers
    Shoes,      // Sneakers, Boots, Sandals
    Outerwear,  // Jackets, Coats, Blazers
    One_Piece,  // Dresses, Jumpsuits, Overalls
    Accessory   // Bags, Scarves, Hats, Belts
}
enum class ItemStatus {
    ACTIVE,   // In closet
    SOLD,     // Good exit (money back)
    DONATED,  // Good exit (sustainable)
    TRASHED,  // Bad exit (landfill)
    WISH_LIST // Optional: If you want to include the "Waiting Room" feature now
}