plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "trust.jesus.discover"
    compileSdk = 36

    defaultConfig {
        applicationId = "trust.jesus.discover"
        minSdk = 28
        targetSdk = 36
        versionName = "V2 11.25"
        versionCode = 2
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    flavorDimensions += "app"
    productFlavors {
        val subDir = "BibleLearn"
        create(subDir) {
            dimension = "app"
            versionName = "V2 11.25"//muss doppelt sonst null Versmemorize.apk
            val compileNum = 2
            val appName = "Bible_learn"
            val apkName = "${appName}_${versionName}_$compileNum.apk"
            //val apkName = "Bible learn.apk"

            buildOutputs.all {
                val variantOutputImpl = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
                variantOutputImpl.outputFileName =  apkName
            }
        }
    }
/*
 productFlavors {
        create("free") {
            dimension = "app"
            val appName = "Free App 2.0"
            //makes new Install..
            manifestPlaceholders["appName"] = appName
            applicationIdSuffix = ".demo"
            versionName = "1.0.0"
            versionNameSuffix = ".3"
            versionCode = (versionName + versionNameSuffix).replace(".", "").toInt()
            val apkName = "${appName}_$versionName$versionNameSuffix($versionCode).apk"

            // change app name block below
            buildOutputs.all {
                val variantOutputImpl = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
                variantOutputImpl.outputFileName =  apkName
            }
        }
    }
 */

    buildTypes {
        release {
            //isMinifyEnabled = true
            //isShrinkResources = true
            //neversionNameSuffix = "-MyNiceDebugModeName"

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

        }
        debug {
            //isMinifyEnabled = false
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
    kotlin {
        compilerOptions {
            jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11
        }
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
        compose = true
    }
}

dependencies {
/*
code optimierung:
https://developer.android.com/topic/performance/app-optimization/enable-app-optimization?hl=de
            isMinifyEnabled = true  bei true unter 4MB aber crash
            isShrinkResources = true
mit Anpassung geht bei 6.2MB statt 25.8MB 09.25 auf Vivo geht

    implementation(libs.layouts)  //!!!!!!! =FlowLayout  !!!!!!!!!!!
    implementation(libs.gson)
    implementation(libs.okhttp) ne
implementation("com.squareup.okhttp3:okhttp:4.11.0")

 */
    implementation(libs.gson)
    implementation(libs.okhttp.v4110)
    implementation(libs.layouts)  //!!!!!!! =FlowLayout  !!!!!!!!!!!

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.preference)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.coordinatorlayout)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)


}