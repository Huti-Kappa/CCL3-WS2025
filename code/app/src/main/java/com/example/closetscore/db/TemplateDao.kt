package com.example.closetscore.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TemplateDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTemplate(templateEntity: TemplateEntity): Long

    @Query("DELETE FROM templates WHERE id = :templateId")
    suspend fun deleteTemplate(templateId: Int)

    @Query("SELECT * FROM templates WHERE status = 'ACTIVE'")
    fun getAllTemplates(): Flow<List<TemplateEntity>>

    @Query("UPDATE templates SET wearCount = wearCount + 1 WHERE id = :templateId")
    suspend fun incrementWearCount(templateId: Int)

    @Transaction
    @Query("SELECT * FROM templates WHERE status = 'ACTIVE'")
    fun getAllTemplatesWithItems(): Flow<List<TemplateWithItems>>

    @Transaction
    @Query("SELECT * FROM templates WHERE id = :templateId")
    suspend fun getTemplateWithItems(templateId: Int): TemplateWithItems?

    @Update
    suspend fun updateTemplate(templateEntity: TemplateEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTemplateItemCrossRef(crossRef: TemplateItemCrossRef)

    @Query("DELETE FROM template_item_cross_ref WHERE templateId = :templateId AND itemId = :itemId")
    suspend fun removeItemFromTemplate(templateId: Int, itemId: Int)

    @Query("DELETE FROM template_item_cross_ref WHERE templateId = :templateId")
    suspend fun removeAllItemsFromTemplate(templateId: Int)
}