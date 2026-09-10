# Consumer Proguard rules for :infra:core-storage

-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Keep AndroidX Security Crypto components (MasterKey, EncryptedSharedPreferences)
-keep class androidx.security.crypto.** { *; }
-dontwarn androidx.security.crypto.**

# Keep DataStore Preferences
-keep class androidx.datastore.preferences.protobuf.** { *; }
-dontwarn androidx.datastore.preferences.protobuf.**
