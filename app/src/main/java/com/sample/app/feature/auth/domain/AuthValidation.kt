package com.sample.app.feature.auth.domain

/**
 * Shared validation thresholds for the auth use cases.
 *
 * Keeping them in one place means login, signup, password reset and
 * change-password all enforce the exact same rules — change a limit here and
 * every flow follows, instead of hunting down a hard-coded number per file.
 */
internal object AuthValidation {
    /** Minimum length for any password the app will accept. */
    const val MIN_PASSWORD_LENGTH = 8

    /** Accepted length range for a one-time passcode. */
    const val OTP_MIN_LENGTH = 4
    const val OTP_MAX_LENGTH = 6
}
