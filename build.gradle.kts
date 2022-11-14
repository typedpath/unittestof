import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
val groupId: String by project
val currentVersion: String by project

plugins {
    kotlin("jvm") version "1.7.10"
}

group = groupId
version = currentVersion

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.11"
}