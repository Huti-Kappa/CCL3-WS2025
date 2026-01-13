package com.example.closetscore.db

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class TemplateWithItems(
    @Embedded val template: TemplateEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            TemplateItemCrossRef::class,
            parentColumn = "templateId",
            entityColumn = "itemId"
        )
    )
    val items: List<ItemEntity>
)