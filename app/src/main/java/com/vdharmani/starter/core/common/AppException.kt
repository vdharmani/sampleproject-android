package com.vdharmani.starter.core.common

/**
 * Base for app-level domain failures. Subclasses live in feature modules
 * (e.g. `InvalidEmailException` in feature:auth's domain). Keep this in
 * core:common so cross-cutting code (interceptors, error renderers) can
 * dispatch on the base type without depending on every feature.
 */
abstract class AppException(message: String? = null, cause: Throwable? = null) :
    Exception(message, cause)

/** Network is unreachable or the request timed out. */
class NetworkException(message: String? = null, cause: Throwable? = null) :
    AppException(message ?: "Network unreachable", cause)

/** Auth credentials missing/expired and refresh failed. UI typically navigates to login. */
class UnauthorizedException(message: String? = null, cause: Throwable? = null) :
    AppException(message ?: "Authentication required", cause)

/** Server returned a 4xx/5xx that isn't 401. */
class HttpException(val code: Int, message: String? = null, cause: Throwable? = null) :
    AppException(message ?: "Server returned $code", cause)
