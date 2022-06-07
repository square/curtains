plugins {
  id("com.android.application")
  kotlin("android")
}

android {
  compileSdkVersion(30)

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
  }

  defaultConfig {
    minSdkVersion(21)
    targetSdkVersion(30)
    applicationId = "com.squareup.curtains.sample"
  }
  
  buildTypes {
    getByName("release") {
      signingConfig = signingConfigs.getByName("debug")
      minifyEnabled(true)
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
    }
  }
}

dependencies {
  implementation(project(":curtains"))
  implementation(Dependencies.AppCompat)
}
