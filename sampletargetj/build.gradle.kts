plugins {
    java
}

val useUnitTestOf = true
val useUnitTestOfExtra = true
val useMockito = true

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")
    if(useUnitTestOf || useUnitTestOfExtra) testImplementation( "com.typedpath.unittestof:annotation:1.0-SNAPSHOT")
    //testImplementation("cglib:cglib:3.2.4")
    if(useUnitTestOf || useUnitTestOfExtra) testAnnotationProcessor  ("com.typedpath.unittestof:kspprocessor:1.0-SNAPSHOT")
    if (useMockito) testImplementation("org.mockito:mockito-all:1.9.5")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    doFirst {
        options.compilerArgs.add("-AUnitTestOf_emitJava=true")
        options.compilerArgs.add("-parameters")

        //options.compilerArgs.add("-Akapt.kotlin.generated=${buildDir.path}/generated/unittestofsource/test/kotlin")
        //println("AnnotationProcessorPath for $name is ${options.annotationProcessorPath?.files}")
        //println("added \"-Akapt.kotlin.generated=${buildDir.path}/generated/unittestofsource/test/kotlin\" for ${this.name}")
    }
}


sourceSets {
    test {
        java {
            if (useMockito) srcDir(file("src/test/mockito/java"))
            if (useUnitTestOf) srcDir(file("src/test/unittestof/java"))
            if (useUnitTestOfExtra) srcDir(file("src/test/unittestofextra/java"))
         }
    }
}


