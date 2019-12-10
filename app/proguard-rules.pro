-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-dontwarn kotlin.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

-keep class com.example.vicky.androidui.Model.** {*;}
#-keep class com.example.vicky.androidui.Model.Request {*;}

## -------------Begin: Retrofit2 ---
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepattributes *Annotation*

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
-keepclassmembernames interface * {
        @retrofit.http.* <methods>;
}

## -------------End: Retrofit2 ---

-keep class com.yandex.metrica.** { *; }
-dontwarn com.yandex.metrica.**
-keepclassmembers class * implements java.io.Serializable {
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepnames class com.facebook.FacebookActivity
-keepnames class com.facebook.CustomTabActivity

-keep class com.facebook.all.All

-keep public class com.android.vending.billing.IInAppBillingService {
    public static com.android.vending.billing.IInAppBillingService asInterface(android.os.IBinder);
    public android.os.Bundle getSkuDetails(int, java.lang.String, java.lang.String, android.os.Bundle);
}