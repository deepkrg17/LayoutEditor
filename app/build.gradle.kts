
plugins {
    id("com.android.application")
    id("com.google.android.gms.oss-licenses-plugin")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.itsvks.editor"
    compileSdk = 33
    buildToolsVersion = "33.0.3"

    defaultConfig {
        applicationId = "com.itsvks.editor"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        
        vectorDrawables { 
            useSupportLibrary = true
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    
    signingConfigs {
        create("default") {
            storeFile = file("../key-layout-editor.keystore")
            storePassword = "itsvks"
            keyAlias = "vivek"
            keyPassword = "itsvks"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("default")
        }
        debug {
            signingConfig = signingConfigs.getByName("default")
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
    
}

dependencies {
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.blankj:utilcodex:1.31.1")
    implementation("androidx.preference:preference:1.2.0")
    implementation("com.github.dhaval2404:imagepicker:2.1")
    implementation("com.google.android.gms:play-services-oss-licenses:17.0.0")
    implementation("com.google.android.gms:play-services-ads:22.2.0")
    implementation("com.google.code.gson:gson:2.10")
    implementation("org.apache.commons:commons-text:1.9")
    
    implementation(platform("com.google.firebase:firebase-bom:32.1.1"))
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-crashlytics")
    
    implementation(project(":vectormaster"))
}
