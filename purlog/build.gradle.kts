import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    id("module.publication")
}

kotlin {
    jvm()
    // android target
    androidTarget {
        publishLibraryVariants("release")
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }
    // iOS targets
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    // macOS targets
    macosX64()
    macosArm64()

    // watchOS targets
    watchosX64()
    watchosArm64()
    watchosSimulatorArm64()

    // tvOS targets
    tvosX64()
    tvosArm64()
    tvosSimulatorArm64()

    // linux targets
    linuxX64()


    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        val appleMain by creating {
            dependsOn(commonMain)
        }

        // iOS targets share appleMain
        val iosX64Main by getting { dependsOn(appleMain) }
        val iosArm64Main by getting { dependsOn(appleMain) }
        val iosSimulatorArm64Main by getting { dependsOn(appleMain) }

        // macOS targets share appleMain
        val macosX64Main by getting { dependsOn(appleMain) }
        val macosArm64Main by getting { dependsOn(appleMain) }

        // watchOS targets share appleMain
        val watchosX64Main by getting { dependsOn(appleMain) }
        val watchosArm64Main by getting { dependsOn(appleMain) }
        val watchosSimulatorArm64Main by getting { dependsOn(appleMain) }

        // tvOS targets share appleMain
        val tvosX64Main by getting { dependsOn(appleMain) }
        val tvosArm64Main by getting { dependsOn(appleMain) }
        val tvosSimulatorArm64Main by getting { dependsOn(appleMain) }
    }
}

android {
    namespace = "com.metashark.purlog.kotlin.sdk"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}