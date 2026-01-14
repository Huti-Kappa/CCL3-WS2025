package com.example.closetscore.data

import com.example.closetscore.db.TemplateDao
import com.example.closetscore.db.TemplateEntity
import com.example.closetscore.db.TemplateItemCrossRef
import kotlinx.coroutines.flow.map

class TemplateRepository(private val templateDao: TemplateDao) {

    val templates = templateDao.getAllTemplates().map { templateList ->
        templateList.map { entity ->
            Template(
                id = entity.id,
                name = entity.name,
                date = entity.date,
                wearCount = entity.wearCount,
                status = entity.status
            )
        }
    }

    val templatesWithItems = templateDao.getAllTemplatesWithItems()

    suspend fun addTemplate(templateEntity: TemplateEntity): Long {
        return templateDao.addTemplate(templateEntity)
    }

    suspend fun updateTemplate(templateEntity: TemplateEntity) {
        templateDao.updateTemplate(templateEntity)
    }

    suspend fun deleteTemplate(templateEntity: TemplateEntity) {
        templateDao.deleteTemplate(templateEntity)
    }
    suspend fun incrementWearCount(templateId: Int){
        templateDao.incrementWearCount(templateId)
    }

    suspend fun addItemToTemplate(templateId: Int, itemId: Int) {
        templateDao.addTemplateItemCrossRef(TemplateItemCrossRef(templateId, itemId))
    }

    suspend fun removeItemFromTemplate(templateId: Int, itemId: Int) {
        templateDao.removeItemFromTemplate(templateId, itemId)
    }



    suspend fun getTemplateWithItems(templateId: Int) = templateDao.getTemplateWithItems(templateId)
}