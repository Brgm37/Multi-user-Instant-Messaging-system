plugins {
	kotlin("jvm") version "2.0.20"
	kotlin("plugin.serialization") version "1.8.10"
}

group = "org.example"
version = "unspecified"

repositories {
	mavenCentral()
}

dependencies {
	testImplementation(kotlin("test"))
}

tasks.test {
	useJUnitPlatform()
}
kotlin {
	jvmToolchain(21)
}
