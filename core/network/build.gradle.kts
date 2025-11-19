plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.kotlinx.serialization)
}

android {
    namespace = "com.droidnotes.core.network"
    compileSdk = 36

    defaultConfig {
        minSdk = 23
        val API_BASE_URL: String = properties.getOrDefault("API_BASE_URL", "") as String
        val NEWS_API_KEY: String = properties.getOrDefault("NEWS_API_KEY", "") as String
        buildConfigField("String", "API_BASE_URL", API_BASE_URL)
        buildConfigField("String", "NEWS_API_KEY", NEWS_API_KEY)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions { jvmTarget = "17" }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugar)
    implementation(project(":common:android"))
    implementation(project(":common:kotlin"))

    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization.converter)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)
}
