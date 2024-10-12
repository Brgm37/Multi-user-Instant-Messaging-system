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
	api(project(":domain"))
	implementation(project(":repository"))
	implementation("com.zaxxer:HikariCP:5.1.0")
	implementation("jakarta.inject:jakarta.inject-api:2.0.1")
	implementation(group = "org.postgresql", name = "postgresql", version = "42.+")
}

tasks.test {
	useJUnitPlatform()
// 	environment(
// 		"DB_URL",
// 		"jdbc:postgresql://localhost:5433/daw_test?user=postgres&password=password",
// 	)
// 	dependsOn(":repository_jdbc:dbTestsWait")
// 	finalizedBy(":repository_jdbc:dbTestsDown")
}
//
// val composeFileDir: Directory = rootProject.layout.projectDirectory.dir("repository_jdbc")
// val dockerComposePath = composeFileDir.file("docker-compose.yml").toString()
//
// task<Exec>("dbTestsUp") {
// // 	commandLine(
// // 		"ls",
// // 		"-l",
// // 		dockerComposePath,
// // 	)
// 	commandLine(
// 		"docker",
// 		"compose",
// 		"-f",
// 		dockerComposePath,
// 		"up",
// 		"-d",
// 		"--build",
// 		"--force-recreate",
// 		"db-tests",
// 	)
// }
//
// task<Exec>("dbTestsWait") {
// 	commandLine(
// 		"docker",
// 		"exec",
// 		"db-tests",
// 		"/app/bin/wait-for-postgres.sh",
// 		"localhost",
// 	)
// 	dependsOn("dbTestsUp")
// }
//
// task<Exec>("dbTestsDown") {
// 	commandLine(
// 		"docker",
// 		"compose",
// 		"-f",
// 		dockerComposePath,
// 		"down",
// 		"db-tests",
// 	)
// }

kotlin {
	jvmToolchain(21)
}
