# Consumer Proguard rules for :infra:core-database

# Keep Room generated classes and implementations
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>();
}
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Keep SQLCipher native database components
-keep class net.sqlcipher.** { *; }
-keep class net.sqlcipher.database.** { *; }
-dontwarn net.sqlcipher.**

# Keep Room TypeConverters
-keepclassmembers class * {
    @androidx.room.TypeConverter *;
}

# Keep DAO interfaces
-keep interface * {
    @androidx.room.Dao *;
}
