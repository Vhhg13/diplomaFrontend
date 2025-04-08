import java.net.InetAddress
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

apply<HiltPlugin>()
apply<KotlinxJson>()

android {
    namespace = "tk.vhhg.hvacapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "tk.vhhg.hvacapp"
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
        debug {
            val local = true

            val props = Properties()
            props.load(project.rootProject.file("local.properties").inputStream())
            val proto = props.getProperty("server.protocol")
            val ip = props.getProperty("server.ip")
            val port = props.getProperty("server.port")
            if (local) {
                buildConfigField("String", "SERVER_ADDRESS", "\"$proto://${InetAddress.getLocalHost().hostAddress}:$port\"")
            } else {
                buildConfigField("String", "SERVER_ADDRESS", "\"$proto://$ip:$port\"")
            }
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
        buildConfig = true
        compose = true
    }
}

dependencies {
    // Compose
    implementation(platform(libs.androidx.compose.bom))
    compose()
    implementation(libs.androidx.navigation.compose)

    // Basic
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)

    // Ktor
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.auth)
    implementation(libs.ktor.client.logging)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Projects
    implementation(project(":im"))
    implementation(project(":auth"))
    implementation(project(":theme"))
    implementation(project(":knob"))
    implementation(project(":rooms"))
    implementation(project(":specific_room"))

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
}