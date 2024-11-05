import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    kotlin("plugin.serialization") version "2.0.20"
    alias(libs.plugins.androidLibrary)
    id("module.publication")
    id("maven-publish")
}

kotlin {
    //jvm()
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
    //linuxX64()


    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
                implementation("io.ktor:ktor-client-core:3.0.0") // Core Ktor Client
                implementation("io.ktor:ktor-client-content-negotiation:3.0.0") // Content Negotiation
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
                implementation("io.ktor:ktor-serialization-kotlinx-json:3.0.0") // JSON Serialization
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-cio:3.0.0")  // JVM/Android engine
                implementation("androidx.security:security-crypto:1.1.0-alpha06")
            }
        }

        val appleMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation("io.ktor:ktor-client-darwin:3.0.0")
            }
        }

        val iosMain by creating {
            dependsOn(appleMain)
        }

        /*val jvmMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-cio:3.0.0")  // JVM/Android engine
            }
        }*/

        // iOS targets share appleMain
        val iosX64Main by getting { dependsOn(iosMain) }
        val iosArm64Main by getting { dependsOn(iosMain) }
        val iosSimulatorArm64Main by getting { dependsOn(iosMain) }

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

        //val linuxX64Main by getting {
        //    kotlin.srcDir("src/linuxX64Main/kotlin")
        //}


        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0-RC.2")
            }
        }

        /*val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit")) // JUnit support for JVM
            }
        }*/
        val androidUnitTest by getting {
            dependencies {
                implementation(kotlin("test-junit")) // JUnit support for Android
            }
        }

        val appleTest by creating {
            dependsOn(commonTest)
        }

        val iosTest by creating {
            dependsOn(appleTest)
        }

        // iOS targets share appleMain
        val iosX64Test by getting { dependsOn(iosTest) }
        val iosArm64Test by getting { dependsOn(iosTest) }
        val iosSimulatorArm64Test by getting { dependsOn(iosTest) }

        // macOS targets share appleMain
        val macosX64Test by getting { dependsOn(appleTest) }
        val macosArm64Test by getting { dependsOn(appleTest) }

        // watchOS targets share appleMain
        val watchosX64Test by getting { dependsOn(appleTest) }
        val watchosArm64Test by getting { dependsOn(appleTest) }
        val watchosSimulatorArm64Test by getting { dependsOn(appleTest) }

        // tvOS targets share appleMain
        val tvosX64Test by getting { dependsOn(appleTest) }
        val tvosArm64Test by getting { dependsOn(appleTest) }
        val tvosSimulatorArm64Test by getting { dependsOn(appleTest) }
    }

    /*linuxX64 {
        binaries {
            executable {
                // Link against OpenSSL
                linkerOpts("-lssl", "-lcrypto")
            }
        }

        compilations["main"].cinterops {
            val openssl by creating {
                defFile = file("src/linuxX64Main/interop/openssl.def")
            }
        }
    }*/
}

android {
    namespace = "io.metashark"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}