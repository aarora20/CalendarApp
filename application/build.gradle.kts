import org.jetbrains.compose.desktop.application.dsl.TargetFormat
val ktor_version: String by project

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose") version "1.5.3"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.10"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    // future use for navigation
//    implementation("com.arkivanov.decompose:decompose:2.0.0")
//    implementation("com.arkivanov.decompose:extensions-compose-jetbrains:2.0.0")
    implementation("org.jetbrains.compose.material3:material3-desktop:1.5.10-beta02")
    implementation("io.ktor:ktor-client-cio-jvm:$ktor_version")
    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("org.reduxkotlin:redux-kotlin-threadsafe:0.5.5")
    implementation("br.com.devsrsouza.compose.icons:tabler-icons:1.1.0")
    implementation(project(mapOf("path" to ":server")))
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "CalendarApp"
            packageVersion = "1.0.0"
        }
    }
}
