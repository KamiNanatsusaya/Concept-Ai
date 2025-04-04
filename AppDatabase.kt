package com.manusai.android.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.manusai.android.data.local.entity.ChatMessageEntity

@Database(entities = [ChatMessageEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
}
