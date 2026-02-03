plugins {
    // AGP 9.0 is the parent plugin; it provides Kotlin support automatically.
    id("com.android.application") version "9.0.0" apply false
    id("com.android.library") version "9.0.0" apply false
    
    // The Compose Compiler is still a separate plugin in 2026.
    id("org.jetbrains.kotlin.plugin.compose") version "2.1.0" apply false
}
