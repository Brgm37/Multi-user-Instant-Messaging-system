plugins {
	kotlin("jvm") version "2.0.20"
}

group = "org.example"
version = "unspecified"

repositories {
	mavenCentral()
}

dependencies {
	api(project(":domain"))
	api(project(":repository"))
	testImplementation(kotlin("test"))
	testImplementation("io.mockk:mockk:1.13.12")
	testImplementation("org.slf4j:slf4j-simple:1.7.32")
	testImplementation("ch.qos.logback:logback-classic:1.5.8")
	api("jakarta.inject:jakarta.inject-api:2.0.1")
}

tasks.test {
	useJUnitPlatform()
}
kotlin {
	jvmToolchain(21)
}
