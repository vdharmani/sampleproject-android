package com.vdharmani.starter.feature.auth.data.mapper

import com.vdharmani.starter.core.database.entity.UserEntity
import com.vdharmani.starter.core.datastore.StoredAuthToken
import com.vdharmani.starter.feature.auth.data.remote.dto.AuthResponseDto
import com.vdharmani.starter.feature.auth.domain.model.AuthToken
import com.vdharmani.starter.feature.auth.domain.model.User

/**
 * Single source of truth for crossing the network ↔ domain ↔ database
 * boundary. When juniors swap to their real backend, this is the file they
 * touch — usually the only one.
 */

fun AuthResponseDto.toAuthToken(): AuthToken = AuthToken(
    accessToken = token,
    // reqres.in doesn't issue refresh tokens — fake one for the demo.
    // Replace `refreshToken ?: ""` with `refreshToken!!` once the real
    // backend returns a proper refresh.
    refreshToken = refreshToken ?: "",
)

fun AuthToken.toStored(): StoredAuthToken = StoredAuthToken(accessToken, refreshToken)

fun AuthResponseDto.toUserOrNull(): User? {
    val effectiveEmail = email ?: return null
    val effectiveId = id?.toString() ?: effectiveEmail   // reqres ids are ints
    return User(
        id = effectiveId,
        email = effectiveEmail,
        name = name ?: effectiveEmail.substringBefore('@'),
        avatarUrl = avatar,
    )
}

fun User.toEntity(): UserEntity = UserEntity(
    id = id,
    email = email,
    name = name,
    avatarUrl = avatarUrl,
)

fun UserEntity.toDomain(): User = User(
    id = id,
    email = email,
    name = name,
    avatarUrl = avatarUrl,
)
