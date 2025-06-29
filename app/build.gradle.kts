plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("kotlin-kapt")
    id("kotlin-parcelize")
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
        buildConfigField("String", "API_CHATBOT", "\"AIzaSyDO13Ra3udlstDrWgYC3Ibs33nRECq_gfI\"" )
        buildConfigField("String", "API_CHATTING", "\"https://stunting-app-chattings-api-vercel.vercel.app/\"")
        buildConfigField("String","SUPABASE_URL_WEBSOCKET", "\"wss://dgcgggdkpswliscglxoa.supabase.co/realtime/v1/websocket?apikey=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImRnY2dnZ2RrcHN3bGlzY2dseG9hIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDE1NzU1ODAsImV4cCI6MjA1NzE1MTU4MH0.cSFoFmPKemPGjho2LlFuW2RhCTb2UNnaGh_B1vQFArE&vsn=1.0.0\"")
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
    // Ui
    implementation(libs.com.github.skydoves.powermenu)
    implementation(libs.com.github.yuyakaido.cardStackView)
    implementation(libs.com.getkeepsafe.taptargetview)
    implementation(libs.com.github.philjay.mpandroidchart)
    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.com.github.evrencoskun)
    implementation(libs.tag.sphere)
    implementation(libs.slide.to.act)
    implementation(libs.glide)
    implementation(libs.androidx.recyclerview)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.datastore.preferences)

    // Retrofit
    implementation(libs.com.squareup.retrofit2.retrofit2)
    implementation(libs.com.squareup.retrofit2.converter.gson2)
    implementation(libs.logging.interceptor)

    implementation(libs.com.airbnb.android.lottie)
    implementation(libs.core.splashscreen)
    implementation(libs.com.github.f0ris.sweetalert)
    implementation(libs.com.google.ai.client.generativeai)
    implementation(libs.circle.imageview)
    // Library excel
    // have to minSdk = 26
    implementation(libs.apache.poi)

    // Library csv
    implementation(libs.kotlin.csv.jvm) // for JVM platform

    // Room
    implementation(libs.room.runtime)
    implementation(libs.androidx.datastore.core.android)
    kapt(libs.room.compiler)

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