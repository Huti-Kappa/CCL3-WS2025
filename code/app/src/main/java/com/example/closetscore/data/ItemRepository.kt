package com.example.closetscore.data

import com.example.closetscore.db.ItemDao
import com.example.closetscore.db.ItemEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.collections.map

class ItemRepository(private val itemDao: ItemDao) {



    suspend fun getItemById(itemId: Int): ItemEntity {
        return itemDao.getItemById(itemId)
    }

    val items = itemDao.getAllItems().map { contactList ->
        contactList.map { entity ->
            Item(
                entity.id,
                entity.name,
                entity.photoUri,
                entity.brand,
                entity.category,
                entity.price,
                entity.isSecondHand,
                entity.wearCount,
                entity.store,
                entity.date,
                entity.status
            )
        }
    }

    suspend fun addItem(itemEntity: ItemEntity) {
        itemDao.addItem(itemEntity)
    }

    suspend fun incrementWearCount(itemId: Int){
        itemDao.incrementWearCount(itemId)
    }

    suspend fun updateItem(itemEntity: ItemEntity){
        itemDao.updateItem(itemEntity)
    }


    suspend fun deleteItem(itemEntity: ItemEntity){
        itemDao.deleteItem(itemEntity)
    }
}