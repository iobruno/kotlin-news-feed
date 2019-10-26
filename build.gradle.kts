import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.3.50"
    id("org.springframework.boot") version "2.1.8.RELEASE"
    id("io.spring.dependency-management") version "1.0.8.RELEASE"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    jacoco
}

group = "io.petproject"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
    jcenter()
}

dependencies {
    val junitVersion: String by project
    val assertJVersion: String by project
    val h2Version: String by project

    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(module = "spring-boot-starter-tomcat")
    }
    implementation("org.springframework.boot:spring-boot-starter-jetty")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.junit.jupiter:junit-jupiter:${junitVersion}")
    testImplementation("org.assertj:assertj-core:${assertJVersion}")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
    }
    runtimeOnly("com.h2database:h2:${h2Version}")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.jacocoTestReport {
    reports {
        csv.isEnabled = false
        html.isEnabled = false
        xml.isEnabled = true
        xml.destination = File("$buildDir/reports/jacoco/report.xml")
    }
}

tasks.jacocoTestCoverageVerification {
    classDirectories.setFrom(
            sourceSets.main.get().output.asFileTree.matching {
                exclude(
                        "io/petproject/ApplicationKt.class",
                        "io/petproject/repository/*.class"
                )
            }
    )
}

val codeCoverage by tasks.registering {
    group = "verification"
    description = "Gradle tests with Code Coverage"

    dependsOn(tasks.test, tasks.jacocoTestReport, tasks.jacocoTestCoverageVerification)

    tasks.findByName("jacocoTestReport")
            ?.mustRunAfter(tasks.findByName("test"))

    tasks.findByName("jacocoTestCoverageVerification")
            ?.mustRunAfter(tasks.findByName("jacocoTestReport"))
}