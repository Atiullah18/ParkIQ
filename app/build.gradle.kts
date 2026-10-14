plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.parkiq"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.parkiq"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
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
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    // =====================================================
    // FIREBASE AI LOGIC
    // =====================================================

    implementation(
        platform(
            "com.google.firebase:firebase-bom:34.19.0"
        )
    )

    implementation(
        "com.google.firebase:firebase-ai"
    )

    implementation(
        "com.google.firebase:firebase-appcheck-debug"
    )

    // =====================================================
    // ANDROIDX
    // =====================================================

    implementation(
        libs.androidx.core.ktx
    )

    implementation(
        libs.androidx.activity.compose
    )

    implementation(
        libs.androidx.lifecycle.runtime.ktx
    )

    // =====================================================
    // COMPOSE
    // =====================================================

    implementation(
        platform(
            libs.androidx.compose.bom
        )
    )

    implementation(
        libs.androidx.compose.ui
    )

    implementation(
        libs.androidx.compose.ui.graphics
    )

    implementation(
        libs.androidx.compose.ui.tooling.preview
    )

    implementation(
        libs.androidx.compose.material3
    )

    implementation(
        "androidx.compose.material:material-icons-extended"
    )

    // =====================================================
    // CAMERAX
    // =====================================================

    implementation("androidx.camera:camera-core:1.4.1")
    implementation("androidx.camera:camera-camera2:1.4.1")
    implementation("androidx.camera:camera-lifecycle:1.4.1")
    implementation("androidx.camera:camera-view:1.4.1")

    // =====================================================
    // ROOM DATABASE
    // =====================================================

    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    ksp("androidx.room:room-compiler:2.6.1")

    // =====================================================
    // GOOGLE MATERIAL
    // =====================================================

    implementation(
        "com.google.android.material:material:1.12.0"
    )

    // =====================================================
    // GOOGLE MAPS
    // =====================================================

    implementation(
        libs.google.maps.compose
    )

    implementation(
        libs.play.services.maps
    )

    implementation(
        libs.play.services.location
    )

    // =====================================================
    // GOOGLE PLACES
    // =====================================================

    implementation(
        "com.google.android.libraries.places:places:4.3.1"
    )

    // =====================================================
    // TESTING
    // =====================================================

    testImplementation(
        libs.junit
    )

    androidTestImplementation(
        platform(
            libs.androidx.compose.bom
        )
    )

    androidTestImplementation(
        libs.androidx.compose.ui.test.junit4
    )

    androidTestImplementation(
        libs.androidx.espresso.core
    )

    androidTestImplementation(
        libs.androidx.junit
    )

    // =====================================================
    // DEBUG
    // =====================================================

    debugImplementation(
        libs.androidx.compose.ui.tooling
    )

    debugImplementation(
        libs.androidx.compose.ui.test.manifest
    )
}