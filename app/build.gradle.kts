plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
}

android {
    namespace = "com.example.assignment3_relationshipcounter"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.assignment3_relationshipcounter"
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.firebase.ui.firestore.v801)
    implementation(libs.firebase.analytics)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.storage)
    implementation(libs.play.services.maps)
    implementation("androidx.work:work-runtime:2.8.1")
    implementation(libs.glide)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)
    implementation(libs.firebase.ui.firestore)
    implementation(libs.mpandroidchart)
//    implementation(libs.prolificinteractive.material.calendarview)
    implementation(libs.legacy.support.v4)
    implementation(libs.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(libs.material.v190)
    implementation(libs.dhaval2404.imagepicker)
    implementation (libs.material.calendar.view)


}