plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.tatsuya.words"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.tatsuya.words"
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)

    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)
    // optional - Kotlin Extensions and Coroutines support for Room
    implementation(libs.androidx.room.room.ktx4)

    // optional - RxJava2 support for Room
    implementation(libs.androidx.room.room.rxjava23)

    // optional - RxJava3 support for Room
    implementation(libs.androidx.room.room.rxjava33)

    // optional - Guava support for Room, including Optional and ListenableFuture
    implementation(libs.androidx.room.room.guava3)

    // optional - Test helpers
    testImplementation(libs.androidx.room.room.testing4)

    // optional - Paging 3 Integration
    implementation(libs.androidx.room.room.paging5)

    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}