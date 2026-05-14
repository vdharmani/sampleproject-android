package com.vdharmani.starter.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vdharmani.starter.feature.auth.presentation.changepassword.ChangePasswordScreen
import com.vdharmani.starter.feature.auth.presentation.deleteaccount.DeleteAccountScreen
import com.vdharmani.starter.feature.auth.presentation.forgot.ForgotPasswordScreen
import com.vdharmani.starter.feature.auth.presentation.login.LoginScreen
import com.vdharmani.starter.feature.auth.presentation.signup.SignupScreen
import com.vdharmani.starter.feature.legal.PrivacyScreen
import com.vdharmani.starter.feature.legal.TermsScreen
import com.vdharmani.starter.feature.premium.presentation.PremiumScreen
import com.vdharmani.starter.feature.profile.presentation.profile.ProfileScreen

/**
 * App-level navigation. Every screen sits behind a typed `composable<…>` route.
 * Cross-screen navigation goes through the `on…` lambdas you pass into the
 * Screen — the Screen never imports [NavController] directly.
 */
@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = RouteLogin,
    ) {
        composable<RouteLogin> {
            LoginScreen(
                onAuthed = { navController.toHomeClearing() },
                onGoSignup = { navController.navigate(RouteSignup) },
                onGoForgot = { navController.navigate(RouteForgot) },
            )
        }

        composable<RouteSignup> {
            SignupScreen(
                onAuthed = { navController.toHomeClearing() },
                onBackToLogin = { navController.popBackStack() },
            )
        }

        composable<RouteForgot> {
            ForgotPasswordScreen(onBackToLogin = { navController.popBackStack() })
        }

        composable<RouteHome> {
            HomeScreen(
                onProfile = { navController.navigate(RouteProfile) },
                onPremium = { navController.navigate(RoutePremium) },
                onTerms = { navController.navigate(RouteTerms) },
                onPrivacy = { navController.navigate(RoutePrivacy) },
            )
        }

        composable<RouteProfile> {
            ProfileScreen(
                onLoggedOut = { navController.toLoginClearing() },
                onChangePassword = { navController.navigate(RouteChangePassword) },
                onDeleteAccount = { navController.navigate(RouteDeleteAccount) },
            )
        }

        composable<RouteChangePassword> {
            ChangePasswordScreen(onBack = { navController.popBackStack() })
        }

        composable<RouteDeleteAccount> {
            DeleteAccountScreen(
                onBack = { navController.popBackStack() },
                onAccountDeleted = { navController.toLoginClearing() },
            )
        }

        composable<RoutePremium> {
            PremiumScreen(onBack = { navController.popBackStack() })
        }

        composable<RouteTerms> {
            TermsScreen(onBack = { navController.popBackStack() })
        }

        composable<RoutePrivacy> {
            PrivacyScreen(onBack = { navController.popBackStack() })
        }
    }
}

@Composable
private fun HomeScreen(
    onProfile: () -> Unit,
    onPremium: () -> Unit,
    onTerms: () -> Unit,
    onPrivacy: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("You're signed in 🎉", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(32.dp))
        Button(onClick = onProfile, modifier = Modifier.fillMaxWidth()) { Text("Profile") }
        Spacer(Modifier.height(12.dp))
        Button(onClick = onPremium, modifier = Modifier.fillMaxWidth()) { Text("Premium") }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onTerms, modifier = Modifier.fillMaxWidth()) { Text("Terms") }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(onClick = onPrivacy, modifier = Modifier.fillMaxWidth()) { Text("Privacy") }
    }
}

// -- nav helpers ------------------------------------------------------------

private fun NavController.toHomeClearing() {
    navigate(RouteHome) {
        popUpTo<RouteLogin> { inclusive = true }
        launchSingleTop = true
    }
}

private fun NavController.toLoginClearing() {
    navigate(RouteLogin) {
        popUpTo<RouteLogin> { inclusive = true }
        launchSingleTop = true
    }
}
