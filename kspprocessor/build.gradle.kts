val groupId: String by project
val currentVersion: String by project
val kspVersion: String by project


plugins {
    kotlin("jvm")
    id("java")
    `maven-publish`
}

java {
    this.targetCompatibility = JavaVersion.VERSION_1_8
}


group = groupId
version = currentVersion

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")
    implementation( project(":annotation"))

}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("kspprocessor") {
            from(components["java"])
        }
        repositories {
            mavenLocal()
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
}
