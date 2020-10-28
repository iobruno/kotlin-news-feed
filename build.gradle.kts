import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    val kotlinVersion = "1.4.10"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    id("org.springframework.boot") version "2.3.4.RELEASE"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
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
    val junitVersion = "5.7.0"
    val assertJVersion = "3.17.2"
    val restAssured = "4.2.0"
    val h2Version = "1.4.197"

    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-web") {
        exclude(module = "spring-boot-starter-tomcat")
    }
    implementation("org.springframework.boot:spring-boot-starter-jetty")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(module = "junit")
    }
    testImplementation("io.rest-assured:rest-assured:$restAssured")
    testImplementation("io.rest-assured:rest-assured-common:$restAssured")
    testImplementation("io.rest-assured:json-path:$restAssured")
    testImplementation("io.rest-assured:xml-path:$restAssured")
    testImplementation("io.rest-assured:kotlin-extensions:$restAssured")
    testImplementation("io.rest-assured:spring-mock-mvc:$restAssured")
    testImplementation("io.rest-assured:spring-mock-mvc-kotlin-extensions:$restAssured")
    testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
    testImplementation("org.junit.vintage:junit-vintage-engine:$junitVersion")
    testImplementation("org.assertj:assertj-core:$assertJVersion")
    runtimeOnly("com.h2database:h2:$h2Version")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "11"
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
                "io/petproject/avidfeed/ApplicationKt.class",
                "io/petproject/avidfeed/repository/*.class"
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
