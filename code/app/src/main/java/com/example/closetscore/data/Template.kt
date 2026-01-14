package com.example.closetscore.data

import com.example.closetscore.db.ItemStatus
import java.time.LocalDate

data class Template(
    val id: Int = 0,
    val name: String,
    val photoUri: String? = null,
    val date: LocalDate,
    val wearCount: Int = 0,
    val status: ItemStatus = ItemStatus.ACTIVE
)