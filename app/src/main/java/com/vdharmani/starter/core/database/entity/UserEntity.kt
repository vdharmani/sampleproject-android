package com.vdharmani.starter.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Persisted user row. This is the DATA-layer representation — pure storage
 * concern. The domain layer's `User` is a separate type; mappers in feature
 * modules convert between them. This separation keeps the schema free to
 * evolve without breaking domain code.
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val email: String,
    val name: String,
    val avatarUrl: String?,
)
