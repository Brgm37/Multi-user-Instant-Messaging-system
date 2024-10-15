plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

group = "org.example"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":httpApi"))
    implementation(project(":http_pipeline"))
    implementation(project(":repository_jdbc"))
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
    environment(
        "DB_URL",
        "jdbc:postgresql://localhost:5433/daw_test",
    )
    environment(
        "DB_USER",
        "postgres",
    )
    environment(
        "DB_PASSWORD",
        "password",
    )
    environment(
        "DB_POOL_SIZE",
        "10",
    )
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    mainClass.set("HttpApiApplicationKt")
}

kotlin {
    jvmToolchain(21)
}