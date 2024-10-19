plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

group = "org.example"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":service"))
    implementation(project(":httpApi"))
    implementation("jakarta.servlet:jakarta.servlet-api:6.1.0")
    implementation("org.springframework:spring-webmvc:6.1.13")
    testImplementation(kotlin("test"))
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    enabled = false
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}