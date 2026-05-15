package com.sample.app.core.network

import com.sample.app.core.common.AppException
import com.sample.app.core.common.HttpException
import com.sample.app.core.common.NetworkException
import com.sample.app.core.common.UnauthorizedException
import java.io.IOException
import retrofit2.HttpException as RetrofitHttpException

// @PublishedApi: referenced from the public `inline fun apiCall`, which is
// inlined at call sites and so cannot see plain private/internal members.
@PublishedApi
internal const val HTTP_UNAUTHORIZED = 401

@PublishedApi
internal const val HTTP_FORBIDDEN = 403

/**
 * Wraps a suspend API call and maps the well-known transport-level
 * exceptions onto the typed hierarchy in core:common.
 *
 * Without this helper, repository code calls `runCatching { api.foo() }` and
 * the consumer's only signal is the raw `IOException` / `retrofit2.HttpException`
 * — losing the ability to dispatch on "this is a network problem" vs.
 * "the server actually said no."
 */
// Boundary catch-all: every failure is converted to a typed Result.failure,
// so catching the generic Exception as the final arm is intentional here.
@Suppress("TooGenericExceptionCaught")
suspend inline fun <T> apiCall(crossinline block: suspend () -> T): Result<T> = try {
    Result.success(block())
} catch (e: IOException) {
    Result.failure(NetworkException(cause = e))
} catch (e: RetrofitHttpException) {
    Result.failure(
        when (e.code()) {
            HTTP_UNAUTHORIZED, HTTP_FORBIDDEN -> UnauthorizedException(cause = e)
            else -> HttpException(code = e.code(), cause = e)
        }
    )
} catch (e: AppException) {
    // Already mapped — let it through.
    Result.failure(e)
} catch (e: Exception) {
    Result.failure(e)
}
