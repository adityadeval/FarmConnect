plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    //Newly added line
    id("com.google.gms.google-services")
    //End of Newly added line
    id("kotlin-parcelize")

    // ("43.") Suggested by GPT. Required for using kapt in the dependencies section for using glide.
    id("kotlin-kapt")
}

android {
    namespace = "ms.cs.farmconnect"
    compileSdk = 34

    defaultConfig {
        applicationId = "ms.cs.farmconnect"
        minSdk = 21
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

    //Newly added line
    implementation(platform("com.google.firebase:firebase-bom:32.7.4"))
    implementation("com.google.firebase:firebase-analytics")
    //End of Newly added line
    //Newly added line
    implementation("com.google.android.material:material:1.0.0")
    //End of Newly added line
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    //Commented below original line
    //implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // Import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))

    // Add the dependency for the Firebase Authentication library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-auth")

    // Declare the dependency for the Cloud Firestore library
    // When using the BoM, you don't specify versions in Firebase library dependencies
    implementation("com.google.firebase:firebase-firestore")

    // ("43.") Below line is required for using Glide to load profile images
    implementation ("com.github.bumptech.glide:glide:4.16.0")
    // ("43.") Suggested by GPT. Used instead of  annotationProcessor.
    kapt ("com.github.bumptech.glide:compiler:4.16.0")
}