package com.sample.app

import android.app.Application
import android.util.Log
import com.sample.app.BuildConfig
import com.vdharmani.subscription.SubscriptionManager
import com.vdharmani.subscription.revenuecat.RevenueCatProvider
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SampleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Subscription provider — RevenueCat under the hood. Junior swaps
        // REVENUECAT_KEY in app/build.gradle.kts to ship a real key.
        //
        // Wrapped defensively: the placeholder key (and any future bad key)
        // makes RevenueCat throw on init. A third-party SDK failing to start
        // must not crash app launch — purchases simply stay unavailable until
        // a real key is in place.
        runCatching {
            SubscriptionManager.initialize(
                RevenueCatProvider(this, BuildConfig.REVENUECAT_KEY)
            )
        }.onFailure {
            Log.w("SampleApplication", "Subscription provider init failed — purchases disabled", it)
        }
    }
}
