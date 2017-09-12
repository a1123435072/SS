# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/zhangfei/Downloads/adt-bundle-mac-x86_64-20140702/sdk/tools/proguard/proguard-android.txt
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

############# Keep line number
-keepattributes SourceFile,LineNumberTable

############# 1Mobile SDK
-keep class com.onemobile.adnetwork.** { *; }

############# Facebook
-keep class com.facebook.model.** { *; }
-keep class com.facebook.internal.** { *; }
-keep class com.facebook.Session { *; }
-keep class com.facebook.Session$AuthorizationRequest  { *; }
-keep class com.github.gorbin.asne.facebook.** { *; }

## Facebook Ads ##
-keep class com.facebook.ads.** { *; }

############ Google
#-dontwarn com.google.ads.afma.nano.**
#-dontwarn com.google.ads.**
#-keep class com.google.ads.** { *; }
#-keepclassmembers class com.google.ads.** {*;}
############# EventBus
-keepclassmembers class ** {
    public void onEvent*(***);
}
# Only required if you use AsyncExecutor
-keepclassmembers class * extends de.greenrobot.event.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

-dontwarn com.google.android.gms.location.**
-dontwarn com.google.android.gms.maps.**
-dontwarn com.google.android.gms.**

############# Joda time
-dontwarn org.joda.time.**

############# Fb get ad type
-keep class com.fw.basemodules.clg.ACt { *; }

############# ExpateAnim
-keep class com.github.florent37.expectanim.core.** { *; }
-dontwarn com.github.florent37.expectanim.core.**


-dontobfuscate

# used in action bar
-keep class android.support.v7.widget.SearchView {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# used for support preferences
-keep class * extends android.support.v7.preference.Preference {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# serializable used in yuku.kpri.* classes
-keepnames class * implements java.io.Serializable

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}



# OkIo referencing Java 7 methods
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement


# As mentioned in picasso homepage.
-dontwarn okhttp3.internal.huc.DelegatingHttpsURLConnection
-dontwarn com.squareup.okhttp.**


# retrolambda
-dontwarn java.lang.invoke.**


# gms analytics still referencing apache http
-dontwarn com.google.android.gms.**
-keep class com.google.android.gms.iid.zzd { *; }
-keep class android.support.v4.content.ContextCompat { *; }

#for solve the uncaught translation error:local variable type mismatch(Translation has been interrupted)
-optimizations !code/allocation/variable



# Retrofit
-keep class com.google.gson.** { *; }
-keep public class com.google.gson.** {public private protected *;}
-keep class com.google.inject.** { *; }
-keep class org.apache.http.** { *; }
-keep class org.apache.james.mime4j.** { *; }
-keep class javax.inject.** { *; }
-keep class javax.xml.stream.** { *; }
-keep class retrofit.** { *; }
-keep class com.google.appengine.** { *; }
-keepattributes *Annotation*
-keepattributes Signature
-dontwarn com.squareup.okhttp.*
-dontwarn rx.**
-dontwarn javax.xml.stream.**
-dontwarn com.google.appengine.**
-dontwarn java.nio.file.**
-dontwarn org.codehaus.**



-dontwarn retrofit2.**
-dontwarn org.codehaus.mojo.**
-keep class retrofit2.** { *; }
-keepattributes Exceptions
-keepattributes RuntimeVisibleAnnotations
-keepattributes RuntimeInvisibleAnnotations
-keepattributes RuntimeVisibleParameterAnnotations
-keepattributes RuntimeInvisibleParameterAnnotations

-keepattributes EnclosingMethod
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclasseswithmembers interface * {
    @retrofit2.* <methods>;
}
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions



# Add any classes the interact with gson
# the following line is for illustration purposes
-keep class com.example.asheq.zanis_postmans.ListAddressesActivity
-keep class com.example.asheq.zanis_postmans.ListOrderActivity
-keep class com.example.asheq.zanis_postmans.LoginActivity
-keep class com.example.asheq.zanis_postmans.SendReportsActivity
-keep class com.example.asheq.track.TrackLocationService
-keep class com.example.asheq.track.TrackLocationApplication
-keep class com.example.asheq.models.** { *; }



# Hide warnings about references to newer platforms in the library
-dontwarn android.support.v7.**
# don't process support library
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.MapActivity
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
# To support Enum type of class members
-keepclassmembers enum * { *; }

-keep class com.activeandroid.** { *; }
-keep class com.activeandroid.**.** { *; }
-keep class * extends com.activeandroid.Model
-keep class * extends com.activeandroid.serializer.TypeSerializer

-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

-dontwarn com.google.android.exoplayer.**
