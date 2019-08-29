# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/jikai/Android/Sdk/tools/proguard/proguard-android.txt
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

-keepattributes SourceFile,LineNumberTable

-keep class com.bestgo.adsplugin.ads.AdAppHelper {
    public *;
}

-keep class com.bestgo.adsplugin.ads.listener.AdStateListener {
    public *;
}
-keep class com.bestgo.adsplugin.ads.listener.AdReadyListener {
    public *;
}
-keep class com.bestgo.adsplugin.ads.listener.ZeroAdUserListener {
    public *;
}
-keep interface com.bestgo.adsplugin.ads.listener.RewardedListener {
    public *;
}
-keep interface com.bestgo.adsplugin.animation.AbstractAnimator {
    public *;
}

-keep class com.bestgo.adsplugin.ads.AdType {
    public *;
}

-keep class com.bestgo.adsplugin.ads.entity.AdUnitMetric {
    public *;
}

-keep class com.bestgo.adsplugin.ads.analytics.** {
    public *;
}
-keep class com.bestgo.adsplugin.ads.listener.NewsListener {
    public *;
}
-keep class com.bestgo.adsplugin.daemon.Daemon {
    public *;
}
-keep interface com.bestgo.adsplugin.ads.activity.ShowAdFilter {
    *;
}
-keep enum com.bestgo.adsplugin.ads.AdNetwork {
    public *;
}
-keep class org.jsoup.** {*;}

-keep class com.facebook.ads.AdListener {*;}
-keep class com.facebook.ads.InterstitialAdListener {*;}

-dontwarn com.flurry.sdk.**


-keep class com.bestgo.adsplugin.ads.logger.EventLogger { public *;}
-keep class ly.count.android.sdk.** { *;}
-keep class org.openudid.** { *;}



# Vungle
-dontwarn com.vungle.**
-dontnote com.vungle.**
-keep class com.vungle.** { *; }
-keep class javax.inject.*

# GreenRobot
-dontwarn de.greenrobot.event.util.**

# RxJava
-dontwarn rx.internal.util.unsafe.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
   rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
   rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
-keep class rx.schedulers.Schedulers { public static <methods>; }
-keep class rx.schedulers.ImmediateScheduler { public <methods>; }
-keep class rx.schedulers.TestScheduler { public <methods>; }
-keep class rx.schedulers.Schedulers { public static ** test(); }

# MOAT
-dontwarn com.moat.**
-keep class com.moat.** { public protected private *; }

# Retrofit
-dontwarn okio.**
-dontwarn retrofit2.Platform$Java8


