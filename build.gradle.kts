plugins {
	kotlin("jvm") version "2.2.21"
	kotlin("plugin.spring") version "2.2.21"
	kotlin("plugin.jpa") version "2.2.21"
	id("org.springframework.boot") version "4.0.7"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "br.pucpr"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.2")
	implementation("tools.jackson.module:jackson-module-kotlin")
	testImplementation("org.springframework.boot:spring-boot-starter-validation-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	// Banco de dados
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-h2console")
	runtimeOnly("com.h2database:h2")
	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")

	// Testes
	val kotest = "6.2.5"
	testImplementation("io.kotest:kotest-runner-junit5-jvm:$kotest")
	testImplementation("io.kotest:kotest-assertions-core:$kotest")
	testImplementation("io.mockk:mockk-jvm:1.14.11")

	// Security
	implementation("org.springframework.boot:spring-boot-starter-security")
	val jjwt = "0.13.0"
	implementation("io.jsonwebtoken:jjwt:$jjwt")
	implementation("io.jsonwebtoken:jjwt-jackson:${jjwt}")
	runtimeOnly("io.jsonwebtoken:jjwt-impl:${jjwt}")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}
