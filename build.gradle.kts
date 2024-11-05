plugins {
    id("com.vanniktech.maven.publish") version "0.30.0" apply false
    //trick: for the same plugin versions in all sub-modules
    alias(libs.plugins.androidLibrary).apply(false)
    alias(libs.plugins.kotlinMultiplatform).apply(false)
}