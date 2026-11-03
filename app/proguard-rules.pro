# Regras de Proguard e R8 para Release

# Manter Entidades do Room
-keep class com.catlabsstudio.guiachevrolet.data.local.entity.** { *; }

# Manter DAOs do Room
-keep interface com.catlabsstudio.guiachevrolet.data.local.dao.** { *; }

# Manter Google Play Core In-App Updates
-keep class com.google.android.play.core.** { *; }

# Manter Google Mobile Ads (AdMob)
-keep class com.google.android.gms.ads.** { *; }
-keep interface com.google.android.gms.ads.** { *; }
