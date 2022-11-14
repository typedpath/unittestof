val groupId: String by project
val currentVersion: String by project

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
    mavenLocal()
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

val sourcesJar by tasks.registering(Jar::class) {
    classifier = "sources"
    from(sourceSets.main.get().allSource)
}


publishing {
    publications {
        create<MavenPublication>("annotations") {
            from(components["java"])
            artifact(sourcesJar.get())
        }
        repositories {
            mavenLocal()
        }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
}
