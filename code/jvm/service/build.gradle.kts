plugins {
	kotlin("jvm") version "1.9.25"
	id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

group = "org.example"
version = "unspecified"

repositories {
	mavenCentral()
}

dependencies {
	api(project(":domain"))
	api(project(":repository"))
	api("jakarta.inject:jakarta.inject-api:2.0.1")
	testImplementation(kotlin("test"))
	testImplementation("io.mockk:mockk:1.13.12")
	testImplementation("org.slf4j:slf4j-simple:1.7.32")
	testImplementation("ch.qos.logback:logback-classic:1.5.8")
	testImplementation(project(":repository_jdbc"))
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
	testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

tasks.test {
	useJUnitPlatform()
//    dependsOn(":repository_jdbc:dbTestWait")
//    finalizedBy(":repository_jdbc:dbTestDown")
}
kotlin {
	jvmToolchain(21)
}
