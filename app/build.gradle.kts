plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.android.kotlin)
    alias(libs.plugins.kapt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
//    alias(libs.plugins.google.gms.google.services)
//    alias(libs.plugins.crash)
}

android {
    namespace = "com.example.notestrack"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.notestrack"
        minSdk = 26
        targetSdk = 35
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
    buildFeatures{
        viewBinding = true
        buildConfig = true
    }

    flavorDimensions.add("default")
    productFlavors{
        create("dev"){
            dimension = "default"
            buildConfigField("String","PHOTO_BASE_URL","\"https://api.pexels.com/v1/\"")
        }
    }
}


dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database.ktx)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.play.services.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    /*hilt*/
    implementation(libs.hilt.android)
    implementation(libs.bundles.navigation.hilt)
    ksp(libs.hilt.android.compiler)
    ksp(libs.androidx.hilt.compiler)
    implementation(libs.androidx.hilt.work)

    /* Retrofit */
    implementation(libs.retrofit2)
    implementation(libs.retrofit2.converter.gson)
    implementation(libs.okhttp3.logging.interceptor)

    // Room
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)
    kapt(libs.androidx.room.compiler)
    annotationProcessor(libs.androidx.room.compiler)
    implementation(libs.bundles.sdp)

    /* Timber */
    implementation(libs.timber)

    implementation(libs.firebase.auth)
    implementation(libs.androidx.core.splashscreen)

    /*lottie*/
    implementation(libs.android.lottie)

    /* Glide */
    implementation(libs.glide)
    implementation(libs.glide.transformations)
    implementation(libs.glide.okhttp3.integration)

    /*firebase*/
    implementation(libs.messaging.ktx)
    implementation(libs.storage.ktx)
    implementation(libs.analytics)
    implementation(libs.inappmessaging.display.ktx)
    implementation(libs.firebase.bom)
    implementation(libs.firebase.database)

    /*data store*/
    implementation(libs.androidx.datastore.preferences)

    /*Media 3*/
    implementation(libs.bundles.media3)

    /*Markdown*/
    implementation (libs.core.mark)

    /*Zoom image*/
    implementation(libs.zoomimage.compose)

    /*Pager*/
    implementation(libs.androidx.paging.runtime.ktx)

    /*Swipe refresh */
    implementation(libs.androidx.swiperefreshlayout)

    implementation(platform(libs.firebase.bom.v3340))

//    implementation(libs.firebase.crashlytics)
//    implementation(libs.analytics)

}