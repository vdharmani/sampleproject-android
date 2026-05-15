package com.vdharmani.starter.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vdharmani.starter.core.database.dao.UserDao
import com.vdharmani.starter.core.database.entity.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
