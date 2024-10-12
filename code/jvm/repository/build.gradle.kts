plugins {
	kotlin("jvm") version "2.0.20"
	id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

group = "org.example"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":domain"))
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
