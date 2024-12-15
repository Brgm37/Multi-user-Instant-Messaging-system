plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.3.3"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

group = "org.example"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":httpApi"))
    implementation(project(":http_pipeline"))
    api(project(":repository_jdbc"))

    implementation("org.postgresql:postgresql:42.7.2")
    implementation("com.zaxxer:HikariCP:5.1.0")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("javax.servlet:javax.servlet-api:4.0.1")

    testImplementation(kotlin("test"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webflux")
}

tasks.test {
    useJUnitPlatform()
    environment(
        "DB_URL_TEST",
        "jdbc:postgresql://localhost:5433/daw_test?user=postgres&password=password",
    )
    environment(
        "DB_URL",
        "jdbc:postgresql://localhost:5433/daw_test",
    )
    environment(
        "DB_USER",
        "postgres",
    )
    environment(
        "DB_PASSWORD",
        "password",
    )
    environment(
        "DB_POOL_SIZE",
        "10",
    )
    environment(
        "AES_KEY",
        "My_Secret_Key",
    )
    dependsOn(":repository_jdbc:dbTestWait")
    finalizedBy(":repository_jdbc:dbTestDown")
}

task<Copy>("extractUberJar") {
    dependsOn("assemble")
    from(
        zipTree(
            layout.buildDirectory
                .file("libs/host-$version.jar")
                .get()
                .toString(),
        ),
    )
    into("build/dependency")
}

val dockerImageJvm = "chimp-jvm"
val dockerImageNginx = "chimp-nginx"
val dockerImagePostgres = "chimp-postgres"

task<Exec>("buildImageJvm") {
    dependsOn("extractUberJar")
    commandLine(
        "docker",
        "build",
        "-t",
        dockerImageJvm,
        "-f",
        "deploy/Dockerfile-jvm",
        ".",
    )
}

task<Copy>("copyWebApp"){
    from("../../js/dist")
    from("../../js/public")
    into("deploy/nginx/public")
}

task<Exec>("buildImageNginx") {
    dependsOn("copyWebApp")
    commandLine(
        "docker",
        "build",
        "-t",
        dockerImageNginx,
        "-f",
        "deploy/Dockerfile-nginx",
        ".",
    )
}

task<Exec>("buildImagePostgres") {
    commandLine(
        "docker",
        "build",
        "-t",
        dockerImagePostgres,
        "-f",
        "deploy/Dockerfile-postgres",
        ".",
    )
}

task("allImages") {
    dependsOn("buildImageJvm")
    dependsOn("buildImageNginx")
    dependsOn("buildImagePostgres")
}

task<Exec>("allUp") {
    commandLine("docker", "compose", "up", "--force-recreate", "-d")
}

task<Exec>("allDown") {
    commandLine("docker", "compose", "down")
}

tasks.withType<org.springframework.boot.gradle.tasks.bundling.BootJar> {
    mainClass.set("HttpApiApplicationKt")
}

kotlin {
    jvmToolchain(21)
}