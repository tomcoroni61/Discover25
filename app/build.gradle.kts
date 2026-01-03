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
        minSdk = 26
        targetSdk = 36 //36 |  35=Min for playstore at 12.25
        versionName = "V16 12.25"
        versionCode = 16  //Playstore will immer "höheren"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    flavorDimensions += "app"
    productFlavors { // neuer Release 1. versionCode +1  2. versionName 2x change here  3. click ignore changes
        val subDir = "BibleLearn"
        create(subDir) {
            dimension = "app"  //versionName 2x here is enough .. // 1x welcome.kt 1x thanks.de = "simple control
            versionName = "V16 12.25"//!!auch .aab  muss doppelt sonst null Versmemorize.apk
            val compileNum = 0
            val appName = "Bible_learn"
            val apkName = "${appName}_${versionName}_$compileNum.apk"
            //val apkName = "Bible learn.apk"

            buildOutputs.all {
                val variantOutputImpl = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
                variantOutputImpl.outputFileName =  apkName
            }
        }
    }
/* AGP 8.12.0
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
            isMinifyEnabled = true  //Themes partly ko?! over app store
            isShrinkResources = true
            //neversionNameSuffix = "-MyNiceDebugModeName"
            //isMinifyEnabled = false
            //isShrinkResources = false
                proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

        }
        debug {
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
ne:
    implementation(libs.html.textview)
    implementation(libs.htmlspanner)
    implementation(libs.htmlcleaner)
    implementation(libs.sakacyber.html.textview)

------------------
    implementation(libs.layouts)  //!!!!!!! =FlowLayout  !!!!!!!!!!!
    implementation(libs.gson)
    implementation(libs.okhttp) ne
implementation("com.squareup.okhttp3:okhttp:4.11.0")
implementation("com.github.sakacyber:html-textview:1.0.15")
implementation(libs.htmlspanner)
    implementation(libs.htmlcleaner)
    implementation("org.jsoup:jsoup:1.21.2")

 */



    implementation(libs.gson)
    implementation(libs.okhttp.v4110)
    implementation(libs.layouts)  //!!!!!!! =FlowLayout  !!!!!!!!!!! import org.apmem.tools.layouts.FlowLayout

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