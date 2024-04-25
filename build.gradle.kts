// Top-level build file where you can add configuration options common to all sub-projects/modules.



buildscript {
    repositories {
        // other repositories...
        mavenCentral()
        google()
        jcenter()
        maven {url = uri ("https://jitpack.io")}
    }
    dependencies {

        classpath("com.google.dagger:hilt-android-gradle-plugin:2.51.1")
        classpath("com.google.gms:google-services:4.4.1")
        val nav_version = "2.7.4"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")

    }


}



plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.jetbrainsKotlinAndroid) apply false
   // id("com.google.gms.google-services") version "4.4.1" apply false
    id("com.google.dagger.hilt.android") version "2.51.1" apply false
}