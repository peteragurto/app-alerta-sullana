plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("androidx.navigation.safeargs.kotlin")
}

android {
    namespace = "com.example.alertasullana"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.alertasullana"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        viewBinding = true
    }
}

dependencies {

    // Fragment - Kotlin
    implementation("androidx.fragment:fragment-ktx:1.5.5")
    implementation("androidx.annotation:annotation:1.7.0")
    implementation("com.google.firebase:firebase-auth:22.2.0")
    //Room - Almacenamiento local
    implementation("androidx.room:room-common:2.6.0")
    //Navegación
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")
    //Firebase Storage y Firestore
    implementation("com.google.firebase:firebase-firestore:24.9.1")
    implementation("com.google.firebase:firebase-storage-ktx:20.3.0")

    // Testing Fragments in Isolation
    debugImplementation("androidx.fragment:fragment-testing:1.5.5")
    // Activity - Kotlin
    implementation("androidx.activity:activity-ktx:1.8.0")
    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    // ViewModel utilities for Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.5.1")
    // LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
    // Corrutinas
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")
    implementation("androidx.lifecycle:lifecycle-runtime:2.6.1")
    //Location
    implementation("com.google.android.gms:play-services-location:21.0.1")

    //Base
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    //bottomsheet
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))

    // base de datos firestore
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth-ktx:22.2.0")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    //Mapa Dependencias
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    //Biblioteca GLide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.github.bumptech.glide:annotations:4.16.0")


}