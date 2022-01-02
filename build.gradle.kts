plugins {
    val kotlinVersion = "1.5.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.8.3"
    id("com.geoffgranum.gradle-conventional-changelog") version "0.3.1"
}

group = "io.github.samarium150"
version = "1.2.1"

repositories {
    mavenLocal()
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-serialization:1.5.4")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().all {
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    kotlinOptions.jvmTarget = "11"
}

changelog {
    appName = project.name
    versionNum = "$version"
    repoUrl = "https://github.com/Samarium150/mirai-console-simple-echo"
    trackerUrl = "https://github.com/Samarium150/mirai-console-simple-echo/issues"
}
