plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)

    //room (DB, JSON 의존성)
    id("com.google.devtools.ksp") version "1.9.20-1.0.14"
    id("kotlin-kapt")
}

android {
    namespace = "com.appliances.recycle"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.appliances.recycle"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    // 뷰 바인딩 설정,
    // 뷰들을 하나의 인스턴스에 담아서, 꺼내서 사용하기. (뷰 선택 쉬움)
    viewBinding {
        enable = true
    }
    //
    buildFeatures{
        viewBinding = true
    }
}

dependencies {

//    implementation(libs.androidx.activity)
//    val roomVersion = "2.5.1"

    //okhttp3
    implementation ("com.squareup.okhttp3:okhttp:4.9.0")
    implementation ("com.squareup.okhttp3:logging-interceptor:4.9.0")

    //room
    implementation("androidx.room:room-runtime:${rootProject.extra["room_version"]}")
    implementation("androidx.core:core-ktx:1.12.0")
    ksp("androidx.room:room-compiler:${rootProject.extra["room_version"]}")
    implementation("androidx.room:room-ktx:${rootProject.extra["room_version"]}")

    // bottom 네비게이션 바
    implementation ("com.google.android.material:material:1.9.0")
    // 뷰페이져2 추가
    implementation ("androidx.viewpager2:viewpager2:1.0.0")
    // 코루틴 추가
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2'")
    // glide 추가
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    // retrofit 추가, gson 컨버터 추가.
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")

    // json 변환하기 위한 라이브러리, gson 예시.
    implementation ("com.google.code.gson:gson:2.8.9")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    // AndroidX Navigation 라이브러리 추가
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")

    // room
    implementation("androidx.room:room-runtime:${rootProject.extra["room_version"]}")
    implementation("androidx.core:core-ktx:1.12.0")
    ksp("androidx.room:room-compiler:${rootProject.extra["room_version"]}")
    implementation("androidx.room:room-ktx:${rootProject.extra["room_version"]}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
    implementation("androidx.activity:activity-ktx:1.7.2")

//    // Room 라이브러리 의존성
//    implementation("androidx.room:room-runtime:$roomVersion")
//    implementation("androidx.room:room-ktx:$roomVersion")
//    ksp("androidx.room:room-compiler:$roomVersion")
//
//    // 기타 라이브러리
//    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
//    implementation("androidx.activity:activity-ktx:1.7.2")
//    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
//    implementation("com.google.android.material:material:1.9.0")
//    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    // 테스트 라이브러리
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // 추가 glide 관련 툴 .
    kapt ("com.github.bumptech.glide:compiler:4.12.0")
    implementation ("com.github.bumptech.glide:okhttp3-integration:4.12.0")

    // webkit, 다음 주소 웹 뷰 사용하기.
    implementation("androidx.webkit:webkit:1.8.0")

    //결제, 포트원, 샘플
    implementation ("com.github.iamport:iamport-android:1.4.5")
    implementation ("com.github.iamport:iamport-android:fix~custom_data-SNAPSHOT")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}