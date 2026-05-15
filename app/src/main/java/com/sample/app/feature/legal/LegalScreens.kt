package com.sample.app.feature.legal

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.sample.app.R

/** Junior swaps these URLs for the project's own hosted pages. */
private const val TERMS_URL = "https://policies.google.com/terms"
private const val PRIVACY_URL = "https://policies.google.com/privacy"

@Composable
fun TermsScreen(onBack: () -> Unit) =
    LegalWebView(title = stringResource(R.string.legal_terms_title), url = TERMS_URL, onBack = onBack)

@Composable
fun PrivacyScreen(onBack: () -> Unit) =
    LegalWebView(title = stringResource(R.string.legal_privacy_title), url = PRIVACY_URL, onBack = onBack)
