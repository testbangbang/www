# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\suicheng\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
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

-keep class com.alibaba.fastjson.** { *; }
-dontwarn com.alibaba.fastjson.**

-keep class com.squareup.** { *; }
-dontwarn com.squareup.**

-keep class com.raizlabs.** { *; }
-dontwarn com.raizlabs.**

-keep class com.google.** { *; }
-dontwarn com.google.**

-dontnote com.google.**
-dontnote com.android.**
-dontnote com.avos.**
-dontnote com.alibaba.fastjson.**
-dontnote android.support.**

-keepnames class com.squareup.**

-dontwarn okio.**
-dontwarn com.squareup.okhttp.**
-dontwarn retrofit2.**

-keepnames class org.greenrobot.eventbus.** { *; }
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }
# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

-keepnames class retrofit2.http.** { *; }

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keep class * implements java.io.Serializable { *; }
