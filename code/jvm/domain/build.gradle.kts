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
	api("org.eclipse.jetty:jetty-security:12.0.10")
	api(group = "org.jetbrains.kotlinx", name = "kotlinx-serialization-json", version = "1.5.+")
}

tasks.test {
	useJUnitPlatform()
}
kotlin {
	jvmToolchain(21)
}
