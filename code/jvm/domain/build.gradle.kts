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
	api("org.eclipse.jetty:jetty-security:12.0.10")
}

tasks.test {
	useJUnitPlatform()
}
kotlin {
	jvmToolchain(21)
}
