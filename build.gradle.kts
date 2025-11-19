import java.util.Properties

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    // Provide explicit versions for plugins used in library modules
    id("com.android.library") version "8.13.1" apply false
    id("org.jetbrains.kotlin.android") version "2.2.21" apply false
    id("org.jetbrains.kotlin.jvm") version "2.2.21" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.21" apply false
    id("com.google.dagger.hilt.android") version "2.57.2" apply false
    id("com.google.devtools.ksp") version "2.2.20-2.0.4" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.7" apply false
}

tasks.register("detektAll") {
    group = "verification"
    description = "Run detekt on all modules"
}

subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")

//    detekt {
//        config = files("${rootProject.projectDir}/config/detekt/detekt.yml")
//        buildUponDefaultConfig = true
//        autoCorrect = true
//    }

    tasks.named("detekt") {
        dependsOn(":detektAll")
    }
}