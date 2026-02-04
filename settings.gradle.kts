dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") } 
        // Optional fallback if jitpack.io times out:
        // maven { url = uri("https://javadoc.jitpack.io") }
    }
}
