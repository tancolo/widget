import com.android.build.gradle.internal.api.VariantFilter
import org.jetbrains.kotlin.ir.backend.js.compile

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.tancolo.widget"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.tancolo.widget"
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
        debug {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }
    }
    buildFeatures {
        buildConfig = true
    }

// IMPORTANT REFERENCE
// https://medium.com/@dimasoktanugraha47/how-to-create-build-types-and-flavors-in-android-kotlin-bb4c41707279
// https://developer.android.com/build/build-variants#kts

    val additionalFlavor = listOf("env", "type")
    flavorDimensions += additionalFlavor
    productFlavors {
        create("development") {
            dimension = "env"
        }
        create("production") {
            dimension = "env"
        }
        create("free") {
            dimension = "type"
            applicationIdSuffix = ".free"
            versionNameSuffix = "_free"
            buildConfigField("Boolean", "SHOW_ADS", "true")
        }
        create("pro") {
            dimension = "type"
            applicationId = "com.tancolo.widget4444"
            buildConfigField("Boolean", "SHOW_ADS", "false")
        }
        //debug productFlavors
        productFlavors.forEach {
            println("===>" + it.toString())
        }
    }

    androidComponents {
        beforeVariants { variantBuilder ->
            // To check for a certain build type, use variantBuilder.buildType == "<buildType>"
            if (variantBuilder.productFlavors.containsAll(listOf("api" to "minApi21", "mode" to "demo"))) {
                // Gradle ignores any variants that satisfy the conditions above.
                variantBuilder.enable = false
            }
        }


//    variantFilter { variant ->
//        def names = variant.flavors*.name
//        if (variant.buildType.name == "release") {
//            if (!names.contains("production")) {
//                setIgnore(true)
//            }
//        }
//    }

//    androidComponents {
//        beforeVariants { variantBuilder ->
//            // To check for a certain build type, use variantBuilder.buildType == "<buildType>"
//            if (variantBuilder.productFlavors.containsAll(listOf("api" to "minApi21", "mode" to "demo"))) {
//                // Gradle ignores any variants that satisfy the conditions above.
//                variantBuilder.enable = false
//            }
//        }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation(project(":customview"))
    implementation(project(":memoryleak"))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}