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
    implementation(project(":repository"))
    testImplementation(kotlin("test"))
	testImplementation("io.mockk:mockk:1.13.12")
	implementation("org.eclipse.jetty:jetty-util:9.4.43.v20210629")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}