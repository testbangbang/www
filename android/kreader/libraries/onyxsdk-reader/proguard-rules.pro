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

-keepattributes Exceptions,Signature,*Annotation*,EnclosingMethod,SourceFile,LineNumberTable

-keep class com.onyx.android.sdk.reader.api.** { public protected *; }
-keep class com.onyx.android.sdk.reader.host.impl.** { public protected *; }
-keep class com.onyx.android.sdk.reader.common.** { public protected *; }
-keep class com.onyx.android.sdk.reader.host.request.** { public protected *; }
-keep class com.onyx.android.sdk.reader.utils.** { public protected *; }
-keep class com.onyx.android.sdk.reader.host.options.** { public protected *; }
-keep class com.onyx.android.sdk.reader.host.wrapper.** { public protected *; }
-keep class com.onyx.android.sdk.reader.reflow.** { public protected *; }
-keep class com.onyx.android.sdk.reader.host.math.** { public protected *; }

-keep class com.onyx.android.sdk.reader.plugins.neopdf.NeoPdfJniWrapper { public protected *; }
-keep class com.onyx.android.sdk.reader.host.layout.LayoutProviderUtils { public protected *; }

-keep class com.onyx.android.sdk.reader.ReaderBaseApp { public protected *; }
-keep class com.onyx.android.sdk.reader.dataprovider.LegacySdkDataUtils { public protected *; }
-keep class com.onyx.android.sdk.reader.dataprovider.ContentSdkDataUtils { public protected *; }
-keep class com.onyx.android.sdk.reader.host.layout.ReaderLayoutManager { public protected *; }
-keep class com.onyx.android.sdk.reader.host.navigation.NavigationArgs { public protected *; }

-keep class com.onyx.android.sdk.data.PageInfo { public protected *; }
-keep class com.onyx.android.sdk.utils.StringUtils { public protected *; }

-keep class com.onyx.android.sdk.reader.utils.GObject { public protected *; }
-keep class com.onyx.android.sdk.reader.reflow.ImageReflowManager { public protected *; }
-keep class com.onyx.android.sdk.reader.reflow.ImageReflowSettings { public protected *; }
-keep class com.onyx.android.sdk.reader.plugins.neopdf.NeoPdfSelection { public protected *; }

-keep class com.onyx.android.sdk.reader.api.ReaderTextSplitter { public protected *; }
-keep class * implements com.onyx.android.sdk.reader.api.ReaderTextSplitter { public protected *; }
-keep class com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContent { public protected *; }
-keep class com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContentEntry { public protected *; }

-keep class com.onyx.android.sdk.reader.host.navigation.NavigationArgs { public protected *; }
-keep class com.onyx.android.sdk.reader.host.navigation.NavigationList { public protected *; }

-keep class com.onyx.android.sdk.reader.api.ReaderSentence { public protected *; }