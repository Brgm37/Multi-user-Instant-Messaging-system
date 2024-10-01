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
	implementation(project(":domain"))
	implementation(project(":repository"))
	implementation(group = "org.postgresql", name = "postgresql", version = "42.+")
	implementation("org.eclipse.jetty:jetty-util:9.4.43.v20210629")
	implementation("jakarta.inject:jakarta.inject-api:2.0.1")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}