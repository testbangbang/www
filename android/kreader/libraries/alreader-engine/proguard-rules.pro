# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /opt/adt-bundle-linux/sdk/tools/proguard/proguard-android.txt
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

-dontwarn com.github.junrar.**

-keep class com.neverland.engbook.forpublic.** { public protected *; }
-keep class org.mozilla.universalchardet.** { public protected *; }

-keep class com.neverland.engbook.bookobj.AlBookEng { public protected *; }
-keep class com.neverland.engbook.util.AlStyles { public protected *; }
-keep class com.neverland.engbook.util.EngBitmap { public protected *; }

-keep enum com.neverland.engbook.bookobj.AlBookEng$** { public protected *; }
-keep class com.neverland.engbook.level1.JEBFilesZIP { public protected *; }
-keep class com.neverland.engbook.unicode.AlUnicode { public protected *; }
-keep class com.neverland.engbook.util.TTFInfo { public protected *; }
-keep class com.neverland.engbook.util.TTFScan { public protected *; }

-keep class com.neverland.engbook.level1.RealCHM { public *; }
