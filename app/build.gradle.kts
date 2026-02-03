plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    // If using Kotlin 2.0+, use the new Compose compiler plugin:
    // id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.example.pipetv"
    compileSdk = 34 // Latest SDK for 2026

    defaultConfig {
        applicationId = "com.example.pipetv"
        minSdk = 26      // Android 8.0 (Required for many TV features)
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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

    composeOptions {
        // Only needed if NOT using the new Kotlin 2.0 compiler plugin
        kotlinCompilerExtensionVersion = "1.5.11" 
    }
}

dependencies {
    // Core Android & Lifecycle
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
    implementation("androidx.activity:activity-compose:1.10.0")

    // --- JETPACK COMPOSE FOR TV ---
    val tvComposeVersion = "1.0.0-alpha11"
    implementation("androidx.tv:tv-foundation:$tvComposeVersion")
    implementation("androidx.tv:tv-material:$tvComposeVersion")

    // --- MEDIA3 / EXOPLAYER (The "Pipe") ---
    val media3Version = "1.9.0" // Stable 2026 release
    implementation("androidx.media3:media3-exoplayer:$media3Version")
    implementation("androidx.media3:media3-ui:$media3Version")
    implementation("androidx.media3:media3-session:$media3Version")
    implementation("androidx.media3:media3-datasource-okhttp:$media3Version")

    // --- NETWORKING (Retrofit) ---
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.14")

    // --- IMAGE LOADING (Coil) ---
    implementation("io.coil-kt.coil3:coil-compose:3.3.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.3.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}
