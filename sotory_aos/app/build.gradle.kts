plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlinSerialization)
    id("com.google.dagger.hilt.android")
    id("com.google.protobuf") version "0.9.4"
    // Add the Google services Gradle plugin
    id("com.google.gms.google-services")

    id("kotlin-parcelize") // add
    id("kotlin-kapt")
}

android {
    namespace = "com.ssafy.sotory"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.ssafy.sotory"
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
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
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
}
val protobufVersion = "3.21.7"

dependencies {

    implementation("io.github.aghajari:LazySwipeCards:1.0.1")

// needs compose-boom 2024.02.01
    implementation(platform("androidx.compose:compose-bom:2024.02.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    implementation ("androidx.constraintlayout:constraintlayout-compose:1.0.1")

    // Image Coil
    implementation("io.coil-kt.coil3:coil-compose:3.1.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.1.0")
    implementation("io.coil-kt.coil3:coil-svg:3.1.0")
    implementation("io.coil-kt.coil3:coil-gif:3.1.0")

    // lottie
    implementation ("com.airbnb.android:lottie-compose:6.6.4")
    // Accompanist Pager 라이브러리
    implementation ("com.google.accompanist:accompanist-pager:0.28.0")
    implementation ("com.google.accompanist:accompanist-pager-indicators:0.28.0")

    // Material 3
    implementation ("androidx.compose.material3:material3:1.1.2")

     implementation(libs.androidx.core.ktx.v1120)  // ✅ Parcelable 확장 함수 포함

    implementation(libs.gson)

    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // ✅ Gson Converter 추가
    // Firebase FCM
    implementation(platform(libs.firebase.bom))
    implementation(libs.material)
    // 추가 아이콘을 위한 라이브러리 (확장 아이콘)
    implementation("androidx.compose.material:material-icons-extended:1.5.4")

    // Permission
    implementation(libs.accompanist.permissions)

    implementation(libs.androidx.material)

    implementation(libs.navigation.compose)


    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.serialization.converter)
    implementation(libs.converter.scalars)
    // Kotlinx-Serialization
    implementation(libs.kotlinx.serialization.json)
    // OkHttp
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    // Lifecycle
    implementation(libs.lifecycle.viewmodel)

    implementation(libs.androidx.hilt.navigation.compose)
    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // splash
    implementation(libs.androidx.core.splashscreen)

    implementation("com.google.protobuf:protobuf-javalite:$protobufVersion")
    // logging
    implementation(libs.timber)
    //hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    implementation(libs.firebase.messaging.ktx)

    // Hilt 테스트 의존성
    androidTestImplementation ("com.google.dagger:hilt-android-testing:2.48")
    kaptAndroidTest ("com.google.dagger:hilt-android-compiler:2.48")

    kapt("com.google.dagger:hilt-android-compiler:2.51.1")

    implementation("com.kakao.sdk:v2-user:2.20.6") // 카카오 로그인 API 모듈

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
//    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // Compose UI 테스트를 위한 의존성
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.1")

    // 디버그 빌드를 위한 테스트 도구
    debugImplementation("androidx.compose.ui:ui-tooling:1.6.1")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.1")

    // 일반 테스트 도구
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test:runner:1.5.2")

    // Espresso 핵심 라이브러리
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // UI 자동화 테스트를 위한 추가 라이브러리
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")

    // 추가 Espresso 기능이 필요한 경우
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
}

// Allow references to generated code
kapt {
    correctErrorTypes = true
}


protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
    plugins {
        generateProtoTasks {
            all().forEach {
                it.builtins {
                    create("java") {
                        option("lite")
                    }
                }
            }
        }
    }
}