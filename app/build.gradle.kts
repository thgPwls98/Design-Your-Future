plugins {
    alias(libs.plugins.android.application)
    // Firebase Google Services 플러그인 추가
    id("com.google.gms.google-services") // Firebase 서비스를 사용하기 위한 플러그인
}

android {
    namespace = "com.example.designyourfuture"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.designyourfuture"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.drawerlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.volley)
    implementation (libs.recyclerview)  // 최신 버전 확인



}