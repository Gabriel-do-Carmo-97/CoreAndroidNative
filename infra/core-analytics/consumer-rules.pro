# Consumer Proguard rules for :infra:core-analytics

-keepattributes Signature
-keepattributes *Annotation*

# Preserve analytics event data classes and consent models
-keepclassmembers class br.com.wgc.core.analytics.** { *; }
