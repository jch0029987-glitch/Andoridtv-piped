pluginManagement {
    repositories {
        google()            // Required for AGP 9.0.0
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Use the Javadoc proxy to avoid the 401 error
        maven { url = uri("https://javadoc.jitpack.io") }
    }
}

rootProject.name = "PipeTV"
include(":app")
