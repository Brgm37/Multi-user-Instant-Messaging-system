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
    implementation(project(":repository"))
    implementation("jakarta.inject:jakarta.inject-api:2.0.1")
    testImplementation(kotlin("test"))
    testImplementation("org.slf4j:slf4j-simple:1.7.32")
    testImplementation("ch.qos.logback:logback-classic:1.5.8")
    testImplementation(group = "org.postgresql", name = "postgresql", version = "42.+")
}

tasks.test {
    useJUnitPlatform()
    environment(
        "DB_URL",
        "jdbc:postgresql://localhost:5433/daw_test?user=postgres&password=password",
    )
    dependsOn(":repository_jdbc:dbTestWait")
    finalizedBy(":repository_jdbc:dbTestDown")
}

val composeFileDir: Directory = rootProject.layout.projectDirectory.dir("repository_jdbc")

val dockerComposePath = composeFileDir.file("docker-compose.yml").toString()

task<Exec>("dbTestUp") {
    commandLine(
        "docker",
        "compose",
        "-f",
        dockerComposePath,
        "up",
        "-d",
        "--build",
        "--force-recreate",
        "db-test",
    )
}

task<Exec>("dbTestWait") {
    commandLine(
        "docker",
        "exec",
        "db-test",
        "/app/bin/wait-for-postgres.sh",
        "localhost",
    )
    dependsOn("dbTestUp")
}

task<Exec>("dbTestDown") {
    commandLine(
        "docker",
        "compose",
        "-f",
        dockerComposePath,
        "down",
        "db-test",
    )
}

kotlin {
    jvmToolchain(21)
}