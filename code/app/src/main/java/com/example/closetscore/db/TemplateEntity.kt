package com.example.closetscore.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "templates")
data class TemplateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val date: LocalDate,
    val status: ItemStatus = ItemStatus.ACTIVE
)
