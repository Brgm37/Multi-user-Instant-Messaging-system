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
	implementation("org.eclipse.jetty:jetty-util:9.4.43.v20210629")
}

tasks.test {
	useJUnitPlatform()
}
kotlin {
	jvmToolchain(21)
}
