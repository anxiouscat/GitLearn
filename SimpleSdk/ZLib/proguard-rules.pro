# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\a_zcg_000\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-printmapping .\build\libs\adsdk.map.txt
-keepattributes SourceFile,LineNumberTable
-dontshrink

-keep public class *$JavaScriptInterface
-keepattributes *$JavaScriptInterface
-keepattributes *Annotation*

-keep class androidx.** {
   *;
}

-keep class com.oversea.ads.base.** {
    <fields>;
    <methods>;
}

-keep class com.oversea.ads.api.** {
    <fields>;
    <methods>;
}

-keep class com.oversea.ads.loaders.** {
    <fields>;
    <methods>;
}

-keep class com.oversea.ads.AdsSDK {
    <methods>;
}

-keep class com.oversea.ads.cfg.Cfg {
    <methods>;
}