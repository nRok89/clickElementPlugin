plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.17.3"
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}
dependencies {
    implementation("org.jetbrains:annotations:24.1.0")
//    implementation(kotlin("stdlib-jdk8"))
//    testImplementation(kotlin("test"))
    testImplementation("com.jetbrains:idea:2023.2")
}
intellij {
    version.set("2023.2")
    type.set("IC") // Target IDE Platform
    plugins.set(listOf("com.intellij.java"))
}

tasks {
    patchPluginXml {
        sinceBuild.set("231") // Ustaw odpowiednią wersję build
        untilBuild.set("233.*") // Zakres wersji build
    }


    verifyPlugin {
        enabled = false
    }
}
