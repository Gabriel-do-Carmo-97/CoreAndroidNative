# Consumer rules for :core:network
-keepattributes Signature
-keepattributes InnerClasses
-keepattributes EnclosingMethod

# Preserve OkHttp internals required for serialization and reflection
-dontwarn okhttp3.**
-dontwarn okio.**
