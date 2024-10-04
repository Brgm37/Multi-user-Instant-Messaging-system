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
	implementation("com.zaxxer:HikariCP:5.0.1")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}