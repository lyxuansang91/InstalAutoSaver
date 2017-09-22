-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends android.app.Activity

-keepattributes Signature
#keep all classes that might be used in XML layouts
-keep public class * extends android.view.View
#-keep public class * extends android.app.Fragment
-keep public class * extends android.support.v4.Fragment


-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}


-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

#keep all public and protected methods that could be used by java reflection
-keepclassmembernames class * {
    public protected <methods>;
}


-dontwarn com.google.**
-keep class com.google.** { *; }


-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

##---------------Begin: Google Play Services ----------
-keep class * extends java.util.ListResourceBundle {
    protected Object getContents();
}

##---------------Begin: proguard configuration for  FONT LIB----------
-dontwarn uk.co.chrisjenx.calligraphy.**
-keep class uk.co.chrisjenx.calligraphy.** { *; }

# Explicitly preserve all serialization members. The Serializable interface
# is only a marker interface, so it wouldn't save them.
-keep class * implements java.io.Serializable { *; }

# Preserve the special static methods that are required in all enumeration classes.
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

##---------------Begin: Prevent Log----------

-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** e(...);
}

-keep class com.polites.android.** { *; }
-dontwarn com.polites.android.**
-keep interface com.polites.android.** { *; }
-keep class org.apache.http.** { *; }
-keep interface org.apache.http.** { *; }
-dontwarn org.apache.http.**



-keep class com.nostra13.universalimageloader.** { *; }
-keep interface com.nostra13.universalimageloader.** { *; }

-keep class de.hdodenhof.** { *; }
-keep interface de.hdodenhof.** { *; }

-keep class org.jsoup.** { *; }
-keep interface org.jsoup.** { *; }

-keep class com.bignerdranch.android.** { *; }
-keep interface com.bignerdranch.android.** { *; }

-keep class org.jbundle.util.osgi.** { *; }
-keep interface org.jbundle.util.osgi.** { *; }

-keep class com.google.** { *; }
-keep interface com.google.** { *; }

-keep class google.** { *; }
-keep interface google.** { *; }

-keep class android.support.** { *; }
-keep interface android.support.** { *; }

-dontwarn android.support.v4.**
# support design
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }
-keepattributes *Annotation*
##---------------Finish: unreachable  ----------