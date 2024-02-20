pluginManagement {
    includeBuild("build-logic")
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
        mavenLocal()
    }
}


rootProject.name = "gptmap"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":core:designsystem")
include(":core:common")
include(":core:data")
include(":core:model")
include(":feature:map")
include(":core:domain")
include(":feature:street-view")
include(":feature:login")
include(":feature:register")
include(":core:google")
include(":feature:forgot-password")
include(":core:mongodb")
include(":feature:favourite")
include(":feature:screenshot")
include(":core:save-screenshot")
include(":feature:screenshot-gallery")
include(":feature:profile")
include(":feature:info")
include(":feature:delete-profile")
include(":feature:verify-auth")
include(":feature:snapTo-script")
include(":core:firebase")
include(":core:gemini")
include(":core:unsplash")
include(":core:testing")
include(":core:datastore")
include(":core:datastore-proto")
include(":benchmarks")
