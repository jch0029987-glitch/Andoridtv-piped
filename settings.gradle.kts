dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        // Official NewPipe fallback
        maven { url = uri("https://repo.recloudstream.org/repository/maven-public/") }
    }
}
