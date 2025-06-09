plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("kotlin-kapt")
}

android {
    // ... other configurations
    buildFeatures {
        //noinspection DataBindingWithoutKapt
        dataBinding = true
        viewBinding = true
    }
    namespace = "com.eibrahim.dizon"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.eibrahim.dizon"
        minSdk = 27
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


}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.legacy.support.v4)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.navigation.runtime.android)
    implementation(libs.androidx.fragment.testing)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.glide)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    implementation(libs.okhttp)
    implementation(libs.core)
    implementation(libs.gson)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx.v276)
    implementation(libs.opencsv)

    implementation(libs.material.v161)

    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)
    kapt(libs.moshi.kotlin.codegen)

    implementation(libs.kotlinx.serialization.json.v160)

    // Retrofit core
    implementation(libs.retrofit.v290)                    // :contentReference[oaicite:0]{index=0}
    // JSON converter (Moshi or Gson)
    implementation(libs.converter.moshi)             // :contentReference[oaicite:1]{index=1}
    // Kotlin coroutines for Retrofit
    implementation(libs.kotlinx.coroutines.core)     // :contentReference[oaicite:2]{index=2}
    implementation(libs.kotlinx.coroutines.android)

    implementation("com.google.android.gms:play-services-auth:20.7.0")

    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    implementation("com.github.bumptech.glide:glide:4.16.0")
    implementation("com.github.bumptech.glide:okhttp3-integration:4.16.0")
    implementation("androidx.activity:activity-ktx:1.9.0")

    implementation(libs.androidx.fragment.testing)
    // Unit testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.7.0")
    testImplementation("com.google.truth:truth:1.1.5")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")

    // Optional for mocking Gson (if needed)
    testImplementation("com.google.code.gson:gson:2.10.1")

    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")


    testImplementation ("junit:junit:4.13.2")
    testImplementation ("org.mockito:mockito-core:4.8.0")
    testImplementation ("androidx.arch.core:core-testing:2.1.0")
//    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    testImplementation ("io.mockk:mockk:1.13.5")

    testImplementation ("org.robolectric:robolectric:4.11.1") // or latest

    testImplementation("junit:junit:4.13.2")
    testImplementation("com.squareup.okhttp3:mockwebserver:4.9.3")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.0.0")


//    // For ViewModel testing
//    testImplementation "junit:junit:4.13.2"
//    testImplementation "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3"
//    testImplementation "androidx.arch.core:core-testing:2.2.0"
//    testImplementation "com.google.truth:truth:1.1.5"
//    testImplementation "io.mockk:mockk:1.13.5"
//
//// For Fragment testing with Robolectric
//    testImplementation "org.robolectric:robolectric:4.10.3"
//    testImplementation "androidx.fragment:fragment-testing:1.6.2"
//    testImplementation "androidx.test:core:1.5.0"
//    testImplementation "androidx.test.ext:junit:1.1.5"
//    debugImplementation "androidx.fragment:fragment-testing:1.6.2" // For debug builds


}