plugins {
	kotlin("jvm") version "2.0.20"
	id("org.springframework.boot") version "3.0.0"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

group = "org.example"
version = "unspecified"

repositories {
	mavenCentral()
}

dependencies {
	implementation(project(":domain"))
	implementation(project(":repository"))
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	testImplementation(kotlin("test"))
}

tasks.test {
	useJUnitPlatform()
}
kotlin {
	jvmToolchain(21)
}
