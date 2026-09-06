# CoreAndroidNative Consumer Proguard Rules

# DataStore
-keep class androidx.datastore.** { *; }

# Security Crypto / EncryptedSharedPreferences
-keep class androidx.security.crypto.** { *; }

# Keep Core Library Public APIs
-keep class br.com.wgc.core.** { *; }
