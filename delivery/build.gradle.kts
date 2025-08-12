plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.delivery"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.delivery"
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
    implementation ("androidx.core:core-splashscreen:1.0.1")
    implementation ("com.airbnb.android:lottie:6.6.0")
    implementation ("androidx.gridlayout:gridlayout:1.0.0")
    implementation ("de.hdodenhof:circleimageview:3.1.0")
    implementation ("com.google.android.gms:play-services-location:21.3.0")
    implementation ("com.airbnb.android:lottie:6.6.0")
    implementation ("com.sun.mail:javax.mail:1.6.2")

}