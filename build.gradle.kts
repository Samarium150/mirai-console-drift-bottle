plugins {
    val kotlinVersion = "1.5.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.serialization") version kotlinVersion

    id("net.mamoe.mirai-console") version "2.8.2"
    id("com.geoffgranum.gradle-conventional-changelog") version "0.3.1"
}

group = "com.github.samarium150"
version = "1.0.1"

repositories {
    mavenLocal()
    maven("https://maven.aliyun.com/repository/public")
    mavenCentral()
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
