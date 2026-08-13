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

        // ZEGOCLOUD
        maven("https://storage.zego.im/maven")

        // JitPack
        maven("https://www.jitpack.io")
    }
}

rootProject.name = "prime"

include(":app")