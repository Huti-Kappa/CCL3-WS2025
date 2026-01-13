package com.example.closetscore.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        ItemEntity::class,
        TemplateEntity::class,
        TemplateItemCrossRef::class
    ],
    version = 2, // Increment version since we're adding new tables
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun itemDao(): ItemDao
    abstract fun templateDao(): TemplateDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                val instance = Room
                    .databaseBuilder(context, AppDatabase::class.java, "closet_database")
                    .fallbackToDestructiveMigration() // This will clear data on version change
                    .build()

                Instance = instance
                return instance
            }
        }
    }
}