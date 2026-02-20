plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.example.pipetv"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.pipetv"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // ─────────────────────────────
    // Core Android & Lifecycle
    // ─────────────────────────────
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
    implementation("androidx.activity:activity-compose:1.12.4")

    // ─────────────────────────────
    // Compose & Navigation
    // ─────────────────────────────
    implementation(platform("androidx.compose:compose-bom:2026.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.9.2")

    // ─────────────────────────────
    // Android TV Specific (Leanback-replacement)
    // ─────────────────────────────
    implementation("androidx.tv:tv-material:1.0.0")
    implementation("androidx.tv:tv-foundation:1.0.0-alpha12")

    // ─────────────────────────────
    // Networking (PDANet / Carrier Concealment)
    // ─────────────────────────────
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("com.squareup.okio:okio:3.9.0") // Crucial for Coil 3 & Disk Cache

    // ─────────────────────────────
    // Coil 3 (Image Loading)
    // ─────────────────────────────
    val coilVersion = "3.0.0-alpha06" // Stable for Coil 3
    implementation("io.coil-kt.coil3:coil-compose:$coilVersion")
    implementation("io.coil-kt.coil3:coil-network-okhttp:$coilVersion")

    // ─────────────────────────────
    // Media3 (The "Workable" ExoPlayer Stack)
    // ─────────────────────────────
    val media3Version = "1.5.1"
    // Core Engine
    implementation("androidx.media3:media3-exoplayer:$media3Version")
    // UI Layer
    implementation("androidx.media3:media3-ui:$media3Version")
    // PDANet/User-Agent Support
    implementation("androidx.media3:media3-datasource-okhttp:$media3Version")
    // Fixes for FAILED_RUNTIME_CHECK
    implementation("androidx.media3:media3-exoplayer-dash:$media3Version")
    implementation("androidx.media3:media3-exoplayer-hls:$media3Version")
}
