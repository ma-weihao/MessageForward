plugins {
    id("com.android.application")
    id("kotlin-kapt")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "cn.quickweather.messageforward"
    compileSdk = 34

    defaultConfig {
        applicationId = "cn.quickweather.messageforward"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

val koinAndroidVersion = "3.5.6"

dependencies {
    api(project(":common"))
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.1")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("androidx.datastore:datastore-preferences:1.1.1")
    implementation(platform("androidx.compose:compose-bom:2024.05.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation(platform("io.insert-koin:koin-bom:$koinAndroidVersion"))
    implementation("io.insert-koin:koin-core")
    implementation("io.insert-koin:koin-android:$koinAndroidVersion")
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")
    testImplementation("junit:junit:4.13.2")
    // Koin Test features
    testImplementation("io.insert-koin:koin-test:$koinAndroidVersion")
    // Koin for JUnit 4
    testImplementation("io.insert-koin:koin-test-junit4:$koinAndroidVersion")
    // Koin for JUnit 5
    testImplementation("io.insert-koin:koin-test-junit5:$koinAndroidVersion")
    // Java Compatibility
    implementation("io.insert-koin:koin-android-compat:$koinAndroidVersion")
    // Jetpack WorkManager
    implementation("io.insert-koin:koin-androidx-workmanager:$koinAndroidVersion")
    // Navigation Graph
    implementation("io.insert-koin:koin-androidx-navigation:$koinAndroidVersion")
    implementation("io.insert-koin:koin-androidx-compose:$koinAndroidVersion")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
//    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}