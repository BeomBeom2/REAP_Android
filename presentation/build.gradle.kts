import java.util.Properties

@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    kotlin("kapt")
    alias(libs.plugins.hilt.android)
}

val properties = Properties()
file("../local.properties").inputStream().use { properties.load(it) }


android {
    namespace = "com.reap.presentation"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.reap.reap_android"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 카카오 로그인
        buildConfigField("String", "KAKAO_REST_API_KEY", "\"${properties["KAKAO_REST_API_KEY"]}\"")
        buildConfigField("String", "KAKAO_API_KEY", "\"${properties["KAKAO_API_KEY"]}\"")
        resValue("string", "KAKAO_REDIRECT_URI", "\"${properties["KAKAO_REDIRECT_URI"]}\"")
        resValue("string", "KAKAO_API_KEY", "\"${properties["KAKAO_API_KEY"]}\"")
    }

    buildFeatures {
        compose = true
        buildConfig = true

    }
    packaging  {
        resources {
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/NOTICE.md"
            excludes += "META-INF/NOTICE.txt"
            excludes += "META-INF/LICENSE-notice.md"
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.2"
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
}

kapt {
    correctErrorTypes = true
}

dependencies {
    implementation(libs.androidx.runtime)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.compiler)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)


    implementation(libs.accompanist.permissions)

    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.serialization.core)

    implementation(libs.androidx.material.icons.core)
    implementation(libs.kakao.user)
    implementation(libs.lottie.compose)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.mockk.android)
    debugImplementation(libs.androidx.ui.test.manifest)

    kapt(libs.hilt.compiler)
    implementation(libs.hilt.android)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.hilt.navigation.compose)
    kapt(libs.androidx.lifecycle.compiler)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.extensions)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(project(":data"))
    implementation(project(":domain"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.calendar.compose)
    implementation(libs.calendar.view)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)
    implementation(libs.okhttp)
    implementation(libs.okhttp.urlconnection)
}