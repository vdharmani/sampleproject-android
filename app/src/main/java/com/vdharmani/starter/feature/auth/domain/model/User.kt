package com.vdharmani.starter.feature.auth.domain.model

/** Domain-level user. Separate from the database `UserEntity` and the API
 *  `UserDto` so each layer can evolve independently. */
data class User(
    val id: String,
    val email: String,
    val name: String,
    val avatarUrl: String? = null,
)
