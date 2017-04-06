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
-keepattributes Exceptions

-keep class com.onyx.android.sdk.reader.api.** { *; }
-keep class com.onyx.android.sdk.reader.host.impl.** { *; }
-keep class com.onyx.android.sdk.reader.common.** { *; }
-keep class com.onyx.android.sdk.reader.host.request.** { *; }
-keep class com.onyx.android.sdk.reader.utils.** { *; }
-keep class com.onyx.android.sdk.reader.host.options.** { *; }
-keep class com.onyx.android.sdk.reader.host.wrapper.** { *; }
-keep class com.onyx.android.sdk.reader.reflow.** { *; }
-keep class com.onyx.android.sdk.reader.host.math.** { *; }

-keepnames class com.onyx.android.sdk.reader.ReaderBaseApp { *; }
-keepnames class com.onyx.android.sdk.reader.reflow.ImageReflowSettings { *; }
-keepnames class com.onyx.android.sdk.reader.plugins.neopdf.NeoPdfSelection { *; }
-keepnames class com.onyx.android.sdk.reader.plugins.neopdf.NeoPdfJniWrapper { *; }
-keepnames class com.onyx.android.sdk.reader.dataprovider.LegacySdkDataUtils { *; }
-keepnames class com.onyx.android.sdk.reader.host.layout.LayoutProviderUtils { *; }
-keepnames class com.onyx.android.sdk.reader.host.layout.ReaderLayoutManager { *; }
-keepnames class com.onyx.android.sdk.reader.host.navigation.NavigationArgs { *; }