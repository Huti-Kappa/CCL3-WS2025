package com.example.closetscore.data

import com.example.closetscore.db.ItemDao
import com.example.closetscore.db.ItemEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ItemRepository(private val itemDao: ItemDao) {
    suspend fun getItemById(itemId: Int): ItemEntity {
        return itemDao.getItemById(itemId)
    }

    val items: Flow<List<Item>> = itemDao.getAllItems().map { entityList ->
        entityList.map { entity ->
            Item(
                id = entity.id,
                name = entity.name,
                photoUri = entity.photoUri,
                brandName = entity.brandName,
                brandType = entity.brandType,
                material = entity.material,
                category = entity.category,
                price = entity.price,
                isSecondHand = entity.isSecondHand,
                wearCount = entity.wearCount,
                dateAcquired = entity.dateAcquired,
                status = entity.status
            )
        }
    }

    suspend fun addItem(item: Item) {
        val entity = ItemEntity(
            id = item.id,
            name = item.name,
            photoUri = item.photoUri,
            brandName = item.brandName,
            brandType = item.brandType,
            material = item.material,
            category = item.category,
            price = item.price,
            isSecondHand = item.isSecondHand,
            wearCount = item.wearCount,
            dateAcquired = item.dateAcquired,
            status = item.status
        )
        itemDao.addItem(entity)
    }

    suspend fun incrementWearCount(itemId: Int){
        itemDao.incrementWearCount(itemId)
    }

    suspend fun deleteItem(itemEntity: ItemEntity){
        itemDao.deleteItem(itemEntity)
    }
}