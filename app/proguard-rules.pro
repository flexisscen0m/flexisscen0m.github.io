-keepattributes *Annotation*
-keep class com.dabtracker.app.data.local.entity.** { *; }
-keep class com.dabtracker.app.data.export.** { *; }
-keepclassmembers class * {
    @kotlinx.serialization.Serializable *;
}
