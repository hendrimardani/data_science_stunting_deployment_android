plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-kapt")
}

android {
    namespace = "com.example.stunting"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.stunting"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "API_KEY", "\"AIzaSyDO13Ra3udlstDrWgYC3Ibs33nRECq_gfI\"" )
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
        mlModelBinding = true
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.com.github.f0ris.sweetalert)
    implementation(libs.com.google.ai.client.generativeai)
    implementation(libs.circle.imageview)
    // Library excel
    // have to minSdk = 26
    implementation(libs.apache.poi)

    // Library csv
    implementation(libs.kotlin.csv.jvm) // for JVM platform

    implementation(libs.room.runtime)
    kapt("androidx.room:room-compiler:2.6.1")

    // Kotlin extnstion for coroutine support with room
    implementation(libs.room.ktx)

    // Kotlin extension for coroutine support with activities
    implementation(libs.activity.ktx)

    implementation(libs.motionToast)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.tensorflow.lite.support)
    implementation(libs.tensorflow.lite.metadata)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}