package com.gramasuvidha.portal.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gramasuvidha.portal.data.entity.AdminEntity
import com.gramasuvidha.portal.data.entity.FeedbackEntity
import com.gramasuvidha.portal.data.entity.ProjectEntity

@Database(
    entities = [ProjectEntity::class, FeedbackEntity::class, AdminEntity::class],
    version = 8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun projectDao(): ProjectDao
    abstract fun feedbackDao(): FeedbackDao
    abstract fun adminDao(): AdminDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "grama_suvidha_v6_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
