plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.bemychef.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.bemychef.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"

        // Arabic and Urdu are shipped languages, not add-ons.
        resourceConfigurations += setOf("en", "ar", "ur")
    }

    signingConfigs {
        val keystorePath = project.findProperty("BEMYCHEF_KEYSTORE_PATH") as String?
        if (keystorePath != null && file(keystorePath).exists()) {
            create("release") {
                storeFile = file(keystorePath)
                storePassword = project.findProperty("BEMYCHEF_KEYSTORE_PASSWORD") as String?
                keyAlias = project.findProperty("BEMYCHEF_KEY_ALIAS") as String?
                keyPassword = project.findProperty("BEMYCHEF_KEY_PASSWORD") as String?
            }
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            isDebuggable = true
            buildConfigField(
                "String",
                "API_BASE_URL",
                "\"${project.findProperty("BEMYCHEF_API_BASE_URL_DEBUG")}\""
            )
        }
        release {
            // Signing is read from local.properties, which is gitignored. When
            // absent the release build fails rather than emitting an unsigned
            // artifact that looks shippable.
            signingConfig = signingConfigs.findByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField(
                "String",
                "API_BASE_URL",
                "\"${project.findProperty("BEMYCHEF_API_BASE_URL_RELEASE")}\""
            )
        }
    }

    buildFeatures {
        buildConfig = true
        viewBinding = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.activity:activity-ktx:1.9.2")
}
