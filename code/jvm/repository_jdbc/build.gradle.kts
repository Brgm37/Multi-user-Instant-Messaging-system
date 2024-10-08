plugins {
    kotlin("jvm") version "2.0.20"
}

group = "org.example"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
	api(project(":domain"))
	implementation(project(":repository"))
	implementation("com.zaxxer:HikariCP:5.1.0")
	implementation("jakarta.inject:jakarta.inject-api:2.0.1")
	implementation(group = "org.postgresql", name = "postgresql", version = "42.+")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}