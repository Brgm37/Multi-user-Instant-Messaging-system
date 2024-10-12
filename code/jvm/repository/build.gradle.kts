plugins {
	kotlin("jvm") version "2.0.20"
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
