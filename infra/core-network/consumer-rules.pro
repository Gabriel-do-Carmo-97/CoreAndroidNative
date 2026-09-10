# Consumer Proguard rules for :infra:core-network

-keepattributes Signature
-keepattributes *Annotation*

# OkHttp & Okio rules for consumers
-dontwarn okhttp3.**
-dontwarn okio.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
