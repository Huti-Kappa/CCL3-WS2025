package com.example.closetscore

import android.app.Application
import com.example.closetscore.data.ItemRepository
import com.example.closetscore.db.AppDatabase

class ClosetApplication : Application() {
    val itemRepository by lazy {

        val db = AppDatabase.getDatabase(this)
        ItemRepository(db.itemDao())
    }
}