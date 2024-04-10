plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
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

    val cameraVersion = "1.3.2"
    val navVersion = "2.7.7"
    val hiltVersion = "2.48"

    //Base
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //Navegación
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")

    //Dagger Hilt
    implementation("com.google.dagger:hilt-android:$hiltVersion")
    kapt("com.google.dagger:hilt-compiler:$hiltVersion")

    //Firebase
    implementation(platform("com.google.firebase:firebase-bom:32.3.1"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    //-> Storage y Firestore
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    //-> Autenticación
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    //Location
    implementation("com.google.android.gms:play-services-location:21.2.0")

    //DataStore
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    //Mapa Dependencias
    implementation("com.google.android.gms:play-services-maps:18.2.0")

    //Bottomsheet
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))

    //Biblioteca GLide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.github.bumptech.glide:annotations:4.16.0")

    //Camera X
    implementation("androidx.camera:camera-core:${cameraVersion}")
    implementation("androidx.camera:camera-camera2:${cameraVersion}")
    implementation("androidx.camera:camera-view:${cameraVersion}")
    implementation("androidx.camera:camera-extensions:${cameraVersion}")
    implementation("androidx.camera:camera-lifecycle:${cameraVersion}")
}