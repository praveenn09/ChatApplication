pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://storage.zego.im/maven")

        // JitPack repository
        maven("https://www.jitpack.io")

    }
}

rootProject.name = "prime"
include(":app")
