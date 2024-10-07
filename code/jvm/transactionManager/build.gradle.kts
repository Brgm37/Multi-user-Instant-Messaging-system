plugins {
    kotlin("jvm") version "2.0.20"
}

group = "org.example"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
	api(project(":repository"))
	implementation(project(":dataBase"))
	implementation("com.zaxxer:HikariCP:5.1.0")
	implementation("org.postgresql:postgresql:42.7.3")
    testImplementation(kotlin("test"))
	implementation("jakarta.inject:jakarta.inject-api:2.0.1")
	implementation("jakarta.enterprise:jakarta.enterprise.cdi-api:4.0.1")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}