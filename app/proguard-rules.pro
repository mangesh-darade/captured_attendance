# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Strip debug attributes to prevent reverse engineering
-repackageclasses ''
-allowaccessmodification
-repackageclasses 'com.example.hidden'

# Strip source file names and line numbers from compiled bytecode
-renamesourcefileattribute SourceFile
-keepattributes !SourceFile,!LineNumberTable

# Ensure Moshi models and Camera2 models are preserved for JSON serialization
-keepclassmembers class * {
    @com.squareup.moshi.Json *;
}


# Moshi reflection models, Retrofit and the WebView bridge must survive shrinking.
-keep class com.example.data.model.** { *; }
-keep class com.example.face.** { *; }
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-keep class kotlin.Metadata { *; }
-keepattributes Signature, *Annotation*, InnerClasses, EnclosingMethod
