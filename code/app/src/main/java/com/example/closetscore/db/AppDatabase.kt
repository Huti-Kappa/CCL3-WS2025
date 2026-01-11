package com.example.closetscore.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ItemEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao

    companion object {
        // @Volatile ensures that changes made to Instance by one thread are visible to all threads immediately.
        @Volatile
        private var Instance: AppDatabase? = null

        // Provides a global access point to your database.
        fun getDatabase(context: Context): AppDatabase {

            // If the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {

                // build database
                val instance = Room
                    .databaseBuilder(context, AppDatabase::class.java, "contact_database")
                    .build()

                // Stores and returns the newly created database instance.
                Instance = instance
                return instance
            }
        }
    }
}