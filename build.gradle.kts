import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.5.30"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.9.2"
    id("com.geoffgranum.gradle-conventional-changelog") version "0.3.1"
}

group = "io.github.samarium150"
version = "1.4.0"

repositories {
    mavenLocal()
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-serialization:1.5.4")
}

tasks {

    withType<KotlinCompile>().all {
        kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
        kotlinOptions.jvmTarget = "11"
    }

    changelog {
        appName = project.name
        versionNum = "$version"
        repoUrl = "https://github.com/Samarium150/mirai-console-simple-echo"
        trackerUrl = "https://github.com/Samarium150/mirai-console-simple-echo/issues"
    }
}
