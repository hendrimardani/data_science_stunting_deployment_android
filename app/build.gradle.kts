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
    }
}

dependencies {
    implementation("de.hdodenhof:circleimageview:3.1.0")
    // Library excel
    // have to minSdk = 26
    implementation("org.apache.poi:poi-ooxml:5.2.2")

    // Library csv
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.9.3") // for JVM platform

    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Kotlin extnstion for coroutine support with room
    implementation("androidx.room:room-ktx:2.6.1")

    // Kotlin extension for coroutine support with activities
    implementation("androidx.activity:activity-ktx:1.8.2")

    implementation("com.github.Spikeysanju:MotionToast:1.4")
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