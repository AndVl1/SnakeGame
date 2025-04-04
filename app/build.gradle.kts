import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.appTracer)
}

android {
    namespace = "ru.andvl.snakegame"
    compileSdk = 35

    defaultConfig {
        applicationId = "ru.andvl.snakegame"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Добавляем BuildConfig поле для AppMetrica
        buildConfigField("String", "APP_METRICA_API_KEY", "\"${System.getenv("APP_METRICA_API_KEY") ?: gradleLocalProperties(rootDir, providers).getProperty("app_metrica_api_key")}\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
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
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    
    // AppCompat
    implementation(libs.androidx.appcompat)
    
    // DataStore
    implementation(libs.androidx.datastore.preferences)
    
    // Kotlinx Serialization
    implementation(libs.kotlinx.serialization.json)
    
    // ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    
    // Decompose
    implementation(libs.decompose)
    implementation(libs.decompose.compose.jetpack)
    
    // MVIKotlin
    implementation(libs.mvikotlin)
    implementation(libs.mvikotlin.main)
    implementation(libs.mvikotlin.coroutines)
    implementation(libs.mvikotlin.logging)
    implementation(libs.mvikotlin.timetravel)

    // Аналитика
    implementation(platform(libs.appTracer.bom))
    implementation(libs.bundles.appTracer.bom)
    implementation(libs.appMetrica.analytics)

    // Тестирование
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlin.reflect)
    
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

tracer {
    create("defaultConfig") {
        pluginToken = System.getenv("TRACER_APP_TOKEN") ?: gradleLocalProperties(rootDir, providers).getProperty("tracer_app_token")
        appToken = System.getenv("TRACER_PLUGIN_TOKEN") ?: gradleLocalProperties(rootDir, providers).getProperty("tracer_plugin_token")
        uploadMapping = true
        uploadNativeSymbols = false
    }
}
