package com.sample.app.feature.auth.domain

import com.sample.app.core.common.AppException

class InvalidEmailException : AppException("Enter a valid email address.")
class WeakPasswordException : AppException("Password must be at least 8 characters.")
class PasswordsDontMatchException : AppException("Passwords don't match.")
class InvalidOtpException : AppException("OTP must be 4-6 digits.")
class EmptyFieldException(field: String) : AppException("$field is required.")
