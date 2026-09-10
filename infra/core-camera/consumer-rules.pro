# Consumer Proguard rules for :infra:core-camera

# CameraX
-keep class androidx.camera.core.** { *; }
-dontwarn androidx.camera.core.**
-keep class androidx.camera.lifecycle.** { *; }
-dontwarn androidx.camera.lifecycle.**
-keep class androidx.camera.view.** { *; }
-dontwarn androidx.camera.view.**

# ML Kit Barcode Scanning
-keep class com.google.mlkit.vision.barcode.** { *; }
-dontwarn com.google.mlkit.vision.barcode.**
