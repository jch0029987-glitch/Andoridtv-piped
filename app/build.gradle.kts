plugins {
    id("com.android.application")
    // Note: AGP 9.0+ handles Kotlin automatically; no need to apply id("kotlin-android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.example.pipetv"
    compileSdk = 36 // Updated for 2026 standards

    defaultConfig {
        applicationId = "com.example.pipetv"
        minSdk = 26
        targetSdk = 35
        versionCode = 3
        versionName = "1.0.3"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            // Use debug signing to avoid needing a release key for personal TV use
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

// Modern Gradle 9.1 syntax for JVM target
kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {
    // Core AndroidX
    implementation("androidx.core:core-ktx:1.17.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.4")
    implementation("androidx.activity:activity-compose:1.12.4")
    
    // Compose BOM (Bill of Materials)
    implementation(platform("androidx.compose:compose-bom:2026.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3:1.4.0")
    implementation("androidx.navigation:navigation-compose:2.9.2")

    // TV Specific Stack (STABLE)
    // We use 1.0.1 which is the verified stable release for TV Material3
    implementation("androidx.tv:tv-material:1.0.1")
    implementation("androidx.tv:tv-foundation:1.0.0-alpha12")

    // Coil 3.3.0 (Optimized for 2026 & Multiplatform)
    implementation("io.coil-kt.coil3:coil-compose:3.3.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.3.0")

    // Media3 & OkHttp Stealth Stack
    // Using 1.5.0+ versions for better codec support in 2026
    val media3Version = "1.5.1"
    implementation("androidx.media3:media3-exoplayer:$media3Version")
    implementation("androidx.media3:media3-ui:$media3Version")
    implementation("androidx.media3:media3-datasource-okhttp:$media3Version")
    
    // Networking
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.google.code.gson:gson:2.11.0")
}
