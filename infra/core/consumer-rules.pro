# CoreAndroidNative Consumer Proguard Rules

# Preserve public library APIs and interfaces while allowing R8 tree-shaking
-keep public class br.com.wgc.core.** {
    public protected *;
}

# Preserve DataStore Preferences key reflection and serialization
-keepclassmembers class * extends androidx.datastore.preferences.core.Preferences {
    public *;
}

# Preserve Security Crypto components used by EncryptedSharedPreferencesCore
-keep class androidx.security.crypto.EncryptedSharedPreferences { *; }
-keep class androidx.security.crypto.MasterKey { *; }
-keep class androidx.security.crypto.MasterKey$* { *; }
