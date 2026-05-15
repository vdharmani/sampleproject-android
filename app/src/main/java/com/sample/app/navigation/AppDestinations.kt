package com.sample.app.navigation

import kotlinx.serialization.Serializable

/** Type-safe Navigation Compose destinations (Kotlin Serialization). */
@Serializable data object RouteLogin

@Serializable data object RouteSignup

@Serializable data object RouteForgot

@Serializable data object RouteHome

@Serializable data object RouteProfile

@Serializable data object RouteChangePassword

@Serializable data object RouteDeleteAccount

@Serializable data object RoutePremium

@Serializable data object RouteTerms

@Serializable data object RoutePrivacy
