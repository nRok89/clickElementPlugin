//plugins {
//    id("java")
//    id("org.jetbrains.kotlin.jvm") version "1.9.0"
//    id("org.jetbrains.intellij") version "1.15.0"
//}
//
//group = "com.click"
//version = "1.0-SNAPSHOT"
//
//repositories {
//    mavenCentral()
//}
dependencies {
    implementation("org.jetbrains:annotations:20.1.0")
}
//// Configure Gradle IntelliJ Plugin
//// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
//intellij {
//    version.set("2023.2.7")
//    type.set("IC") // Target IDE Platform
//    plugins.set(listOf(/* Plugin Dependencies */"java"))
//}
//
//tasks {
//    // Set the JVM compatibility versions
//    withType<JavaCompile> {
//        sourceCompatibility = "17"
//        targetCompatibility = "17"
//    }
//    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
//        kotlinOptions.jvmTarget = "17"
//    }
//
//    patchPluginXml {
//        sinceBuild.set("222")
//        untilBuild.set("232.*")
//    }
//
//    signPlugin {
//        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
//        privateKey.set(System.getenv("PRIVATE_KEY"))
//        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
//    }
//
//    publishPlugin {
//        token.set(System.getenv("PUBLISH_TOKEN"))
//    }
//}

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
