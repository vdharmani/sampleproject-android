package com.sample.app.feature.premium.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sample.app.R
import com.vdharmani.subscription.compose.ComposeSubscription
import com.vdharmani.subscription.model.ProductType
import kotlinx.coroutines.launch

/**
 * Premium / paywall screen.
 *
 * Uses `subscription-android` end-to-end:
 *  - `ComposeSubscription()` returns a manager backed by the RevenueCat
 *    provider that `SampleApplication` registered with `SubscriptionManager`
 *    at process start.
 *  - `customerInfo` is a lifecycle-aware Compose `State<CustomerInfo?>` —
 *    updates automatically on purchase, restore, identity switch.
 *
 * Replace `PREMIUM_PRODUCT_ID` and `PREMIUM_ENTITLEMENT` with the IDs
 * configured in your RevenueCat dashboard.
 */
@Composable
fun PremiumScreen(onBack: () -> Unit) {
    val sub = ComposeSubscription()
    val info by sub.customerInfo
    val isPremium = info?.hasEntitlement(PREMIUM_ENTITLEMENT) == true

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(
                    if (isPremium) R.string.premium_title_member else R.string.premium_title_upsell
                ),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(
                    if (isPremium) R.string.premium_subtitle_member else R.string.premium_subtitle_upsell
                ),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(32.dp))

            val purchaseFailedMsg = stringResource(R.string.premium_purchase_failed)
            val restoreFailedMsg = stringResource(R.string.premium_restore_failed)

            if (!isPremium) {
                Button(
                    onClick = {
                        scope.launch {
                            sub.purchase(PREMIUM_PRODUCT_ID, ProductType.SUBS)
                                .onFailure { e ->
                                    if (e !is com.vdharmani.subscription.PurchaseCancelledException) {
                                        snackbarHostState.showSnackbar(e.message ?: purchaseFailedMsg)
                                    }
                                }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text(stringResource(R.string.premium_subscribe_cta)) }
                Spacer(Modifier.height(12.dp))
            }

            OutlinedButton(
                onClick = {
                    scope.launch {
                        sub.restore().onFailure { e ->
                            snackbarHostState.showSnackbar(e.message ?: restoreFailedMsg)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(R.string.premium_restore)) }

            Spacer(Modifier.height(24.dp))
            TextButton(onClick = onBack) { Text(stringResource(R.string.premium_back)) }
        }
    }
}

private const val PREMIUM_PRODUCT_ID = "premium_monthly"
private const val PREMIUM_ENTITLEMENT = "premium"
