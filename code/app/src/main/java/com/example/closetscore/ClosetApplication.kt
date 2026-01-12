package com.example.closetscore

import android.app.Application
import com.example.closetscore.data.ItemRepository
import com.example.closetscore.data.TemplateRepository
import com.example.closetscore.db.AppDatabase

class ClosetApplication : Application() {
    private val database by lazy { AppDatabase.getDatabase(this) }

    val itemRepository by lazy {
        ItemRepository(database.itemDao())
    }

    val templateRepository by lazy {
        TemplateRepository(database.templateDao())
    }
}