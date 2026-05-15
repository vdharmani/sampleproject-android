# App-specific R8 / ProGuard rules (release). Library AARs merge their own
# consumer rules (Hilt, Retrofit, OkHttp, etc.); this file covers gaps for
# Kotlin Serialization, reflection-style lookups, and readable stack traces.

# --- Stack traces ---
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# --- Kotlin & coroutines ---
-dontwarn kotlinx.coroutines.flow.**
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# --- kotlinx.serialization (Retrofit DTOs, Navigation type-safe routes) ---
# https://github.com/Kotlin/kotlinx.serialization/blob/master/README.md#android
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**

-keepclasseswithmembers class * {
    kotlinx.serialization.KSerializer serializer(...);
}

-if @kotlinx.serialization.Serializable class **
-keepclassmembers class <1> {
    *** Companion;
}
-keepnames class <1>$$serializer
-keepclassmembers class <1>$$serializer {
    *** INSTANCE;
}

# --- Retrofit service (explicit keep for interface + generic signatures) ---
-keep,allowobfuscation,allowshrinking interface com.sample.app.feature.auth.data.remote.AuthApi { *; }
-keepattributes Signature, Exceptions, InnerClasses, EnclosingMethod

# --- Gson / Moshi not used; Retrofit uses kotlinx.serialization ---
