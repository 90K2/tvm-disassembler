import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.1"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
	`maven-publish`
}

group = "org.ton"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenCentral()
	maven { url = uri("https://jitpack.io") }

}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.2")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.3.3")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
	runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.6.2")

	implementation("com.github.andreypfau:curve25519-kotlin:main-SNAPSHOT")
	implementation("com.github.andreypfau:ton-kotlin:main-SNAPSHOT")

}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
val javadocJar by tasks.registering(Jar::class) {
	archiveClassifier.set("javadoc")
}
publishing {
	publications.withType<MavenPublication> {
		artifact(javadocJar.get())
		pom {
			groupId = "org.ton"
			artifactId = "disassembler"
			version = "0.0.1"
		}
	}
}
