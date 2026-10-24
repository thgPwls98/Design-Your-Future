// Project level build.gradle.kts (프로젝트 루트의 build.gradle.kts 파일)
buildscript {
    repositories {
        google()  // Google 리포지토리
        mavenCentral()  // Maven 중앙 리포지토리
    }
    dependencies {
        // Firebase 플러그인 추가
        classpath(libs.google.services.v4315)  // 최신 버전으로 설정
        classpath("com.android.tools.build:gradle:8.7.2")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    // 여기에 다른 플러그인도 추가될 수 있습니다.
}
