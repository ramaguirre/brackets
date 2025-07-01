package com.dentaltracker.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context

@Database(
    entities = [DentalPhoto::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverters::class)
abstract class DentalDatabase : RoomDatabase() {
    abstract fun dentalPhotoDao(): DentalPhotoDao
    
    companion object {
        @Volatile
        private var INSTANCE: DentalDatabase? = null
        
        fun getDatabase(context: Context): DentalDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DentalDatabase::class.java,
                    "dental_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}