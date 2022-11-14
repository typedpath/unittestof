val useKapt = true
// its kapt or ksp
val useKsp = !useKapt
val useMockk = true

plugins {

    kotlin("jvm")
    // uncomment for kapt
    kotlin("kapt") version "1.7.10"
    // uncomment for ksp
    id("com.google.devtools.ksp")
    idea
}

group = "com.typespath.injectwithtest.sampletarget"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation( "com.typedpath.unittestof:annotation:1.0-SNAPSHOT")
    //uncomment for kapt
    if (useKapt) kaptTest ("com.typedpath.unittestof:kspprocessor:1.0-SNAPSHOT")
    //uncomment for ksp
    if ( useKsp ) kspTest("com.typedpath.unittestof:kspprocessor:1.0-SNAPSHOT")
    testImplementation(kotlin("test:1.5.30+") )
    if (useMockk) testImplementation ("io.mockk:mockk:1.9.3")
}


sourceSets {
    test {
        java {
            if (useKsp) srcDir(file("build/generated/ksp/test/kotlin"))
            if (useKsp || useKapt) srcDir(file("src/test/usekotlingen"))
            if (useMockk ) srcDir(file("src/test/usemockk"))
        }
    }
}

idea {
    module {
        // Not using += due to https://github.com/gradle/gradle/issues/8749
        if (useKsp) testSourceDirs = testSourceDirs + file("build/generated/ksp/test/kotlin")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
    kotlinOptions.javaParameters = true
}

