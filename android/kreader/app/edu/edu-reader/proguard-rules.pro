# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/murthy/Library/Android/sdk/tools/proguard/proguard-android.txt
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

-dontshrink

-keep class com.onyx.android.sdk.reader.api.** { public protected *; }
-keepnames class com.onyx.edu.reader.device.DeviceConfig { *; }
-keepnames class com.onyx.edu.reader.ui.data.ReviewDocumentData { *; }
-keepnames class com.onyx.android.sdk.scribble.formshape.FormValue { *; }
-keep class com.onyx.edu.reader.ui.handler.HandlerManager

-keepnames class org.apache.lucene.analysis.** { *; }

-keepnames class com.onyx.android.cropimage.data.** { *; }

-keepnames class com.onyx.android.sdk.data.PageInfo { *; }
-keepnames class com.onyx.android.sdk.utils.StringUtils { *; }

-keepnames class com.onyx.android.sdk.reader.utils.GObject { *; }
-keepnames class com.onyx.android.sdk.reader.reflow.ImageReflowManager { *; }
-keepnames class com.onyx.android.sdk.reader.reflow.ImageReflowSettings { *; }
-keepnames class com.onyx.android.sdk.reader.plugins.neopdf.NeoPdfSelection { *; }

-keepnames class com.onyx.android.sdk.reader.api.ReaderTextSplitter { *; }
-keepnames class * implements com.onyx.android.sdk.reader.api.ReaderTextSplitter { *; }
-keepnames class com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContent { *; }
-keepnames class com.onyx.android.sdk.reader.api.ReaderDocumentTableOfContentEntry { *; }

-keepnames class com.onyx.android.sdk.reader.host.navigation.NavigationArgs { *; }
-keepnames class com.onyx.android.sdk.reader.host.navigation.NavigationList { *; }

-keepnames class com.onyx.android.sdk.reader.api.ReaderSentence { *; }

-keepnames class com.onyx.android.sdk.scribble.utils.** { *; }

# keep classes used by TypeConverter of dbflow
-keep class com.onyx.edu.reader.note.data.ReaderNotePageNameMap { *; }
-keep class com.onyx.android.sdk.scribble.data.PageNameList { *; }
-keep class com.onyx.android.sdk.scribble.data.TouchPointList { *; }

-keepattributes Exceptions,InnerClasses,...
-keepnames class com.onyx.android.sdk.scribble.data.PageNameList { *; }
-keepnames class com.onyx.android.sdk.scribble.utils.MappingConfig { *; }
-keepnames class com.onyx.android.sdk.scribble.utils.MappingConfig$* { *; }
-keepnames class com.onyx.android.sdk.scribble.utils.DeviceConfig { *; }
-keepnames class com.onyx.android.sdk.scribble.utils.DeviceConfig$* { *; }
-keepnames class com.onyx.android.sdk.scribble.data.ShapeExtraAttributes { *; }
