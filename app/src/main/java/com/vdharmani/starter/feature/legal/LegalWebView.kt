package com.vdharmani.starter.feature.legal

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Generic WebView wrapper used for Terms / Privacy / any in-app web content.
 *
 * Compose officially recommends `AndroidView { WebView(it) }` — the
 * accompanist-webview library that everyone used to reach for is deprecated.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalWebView(
    title: String,
    url: String,
    onBack: () -> Unit,
) {
    var loading by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        // Plain ASCII back arrow keeps the dep surface minimal.
                        Text(
                            text = "‹",
                            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
                        )
                    }
                },
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            WebViewHost(url = url, onLoadingChanged = { loading = it })
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun WebViewHost(url: String, onLoadingChanged: (Boolean) -> Unit) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            WebView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView, url: String?, favicon: android.graphics.Bitmap?) {
                        onLoadingChanged(true)
                    }
                    override fun onPageFinished(view: WebView, url: String?) {
                        onLoadingChanged(false)
                    }
                }
                loadUrl(url)
            }
        },
        update = { webView ->
            // Only reload if URL changed — avoids reload on every recomposition.
            if (webView.url != url) webView.loadUrl(url)
        },
    )
}
