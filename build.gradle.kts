import org.apache.tools.ant.filters.ReplaceTokens
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
	id("org.springframework.boot") version "3.2.7"
	id("io.spring.dependency-management") version "1.1.5"


	// https://github.com/n0mer/gradle-git-properties/releases
	id("com.gorylenko.gradle-git-properties") version "2.4.1"

	// https://github.com/spotbugs/spotbugs-gradle-plugin/releases
	id("com.github.spotbugs") version "6.0.7"

	// https://github.com/diffplug/spotless/tree/master/plugin-gradle
	// https://mvnrepository.com/artifact/com.diffplug.spotless/spotless-plugin-gradle
//	id("com.diffplug.spotless") version "6.25.0"

	// https://github.com/researchgate/gradle-release
	// https://mvnrepository.com/artifact/net.researchgate.release/net.researchgate.release.gradle.plugin
	id("net.researchgate.release") version "3.0.2"

	// https://github.com/ben-manes/gradle-versions-plugin/releases
	id("com.github.ben-manes.versions") version "0.51.0"

	java
	idea
	kotlin("jvm")
}

group = "com.msd"
version = "0.0.1-SNAPSHOT"

//java {
//	toolchain {
//		languageVersion = JavaLanguageVersion.of(21)
//	}
//}

configurations {
	all {
		exclude("org.springframework.boot", "spring-boot-starter-logging")

		// Can"t exclude because of this: https://github.com/testcontainers/testcontainers-java/issues/970
		// exclude("junit", "junit")
	}
}
configurations.named("spotbugs").configure {
	resolutionStrategy.eachDependency {
		if (requested.group == "org.ow2.asm") {
			useVersion("9.5")
			because("Asm 9.5 is required for JDK 21 support")
		}
	}
}

repositories {
	mavenLocal()
	mavenCentral()
	google()
}

dependencyManagement {
	imports {
		// https://github.com/spring-projects/spring-boot/releases
		mavenBom("org.springframework.boot:spring-boot-dependencies:3.2.3")

		// To avoid specifying the version of each dependency, use a BOM or Bill Of Materials.
		// https://github.com/testcontainers/testcontainers-java/releases
		mavenBom("org.testcontainers:testcontainers-bom:1.18.3")

		//https://immutables.github.io/
		mavenBom("org.immutables:bom:2.9.2")
	}

	dependencies {
		// https://github.com/apache/logging-log4j2/tags
		dependencySet("org.apache.logging.log4j:2.20.0") {
			entry("log4j-core")
			entry("log4j-api")
			entry("log4j-web")
		}
	}
}


dependencies {
	// https://github.com/spotbugs/spotbugs/tags
	compileOnly("com.github.spotbugs:spotbugs-annotations:4.8.3")
	testCompileOnly("com.github.spotbugs:spotbugs-annotations:4.8.3")

	// https://github.com/KengoTODA/findbugs-slf4j
	spotbugsPlugins("jp.skypencil.findbugs.slf4j:bug-pattern:1.5.0@jar")

	spotbugsPlugins("com.h3xstream.findsecbugs:findsecbugs-plugin:1.12.0")

	annotationProcessor("org.immutables:value")
	compileOnly("org.immutables:builder")
	compileOnly("org.immutables:value-annotations")

	// https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#configuration-metadata-annotation-processor
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

	// https://mapstruct.org/documentation/installation/
	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")


	// libs
	implementation("org.springframework.boot:spring-boot-starter-cache")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")

	// https://mvnrepository.com/artifact/com.google.code.gson/gson
	implementation("com.google.code.gson:gson:2.7")


	// https://mvnrepository.com/artifact/org.json/json
	implementation("org.json:json:20090211")

	// https://mvnrepository.com/artifact/org.modelmapper/modelmapper
	implementation("org.modelmapper:modelmapper:2.1.1")

	// https://mvnrepository.com/artifact/commons-fileupload/commons-fileupload
	implementation("commons-fileupload:commons-fileupload:1.4")

	// https://mvnrepository.com/artifact/net.coobird/thumbnailator
	implementation("net.coobird:thumbnailator:0.4.1")

	// https://mvnrepository.com/artifact/commons-io/commons-io
	implementation("commons-io:commons-io:2.6")

	// https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt-api
	implementation("io.jsonwebtoken:jjwt-api:0.12.5")

	// https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt-impl
	runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")

	// https://mvnrepository.com/artifact/io.jsonwebtoken/jjwt-jackson
	runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.5")

	// https://mvnrepository.com/artifact/me.paulschwarz/spring-dotenv
	implementation("me.paulschwarz:spring-dotenv:4.0.0")

	// https://mvnrepository.com/artifact/org.hibernate/hibernate-core
	implementation("org.hibernate:hibernate-core:6.4.3.Final")

	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.mysql:mysql-connector-j")
	annotationProcessor("org.projectlombok:lombok")

	// Test
//	testImplementation("com.squareup.okhttp3:mockwebserver:3.2.0")
//	implementation("com.squareup.okhttp3:okhttp:3.2.0")
//	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
//	testImplementation("org.junit.jupiter:junit-jupiter-api")
//	testImplementation("org.junit.jupiter:junit-jupiter-params")
//	testImplementation("org.testcontainers:testcontainers")
//	testImplementation("org.testcontainers:junit-jupiter")
//	testImplementation("org.testcontainers:mockserver")
//	testImplementation("org.testcontainers:postgresql")
//	testImplementation("org.testcontainers:rabbitmq")
////	testImplementation("org.mockito:mockito-inline")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")

	implementation("org.springframework.boot:spring-boot-starter-websocket")

	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	implementation("org.springframework.security:spring-security-messaging:6.3.1")
}

spotbugs {
	toolVersion.set("4.7.3")
	excludeFilter.set(file("${project.rootDir}/findbugs-exclude.xml"))
}
//
//spotless {
//	java {
//		// https://github.com/google/google-java-format/releases/latest
//		googleJavaFormat("1.17.0")
//	}
//}

tasks {
	spotbugsMain {
		effort.set(com.github.spotbugs.snom.Effort.MAX)
		reports.create("html") {
			enabled = true
		}
	}

	val bootRun by getting(BootRun::class) {
		jvmArgs = listOf("-Duser.timezone=Asia/Tashkent")
	}

	spotbugsTest {
		ignoreFailures = true
		reportLevel.set(com.github.spotbugs.snom.Confidence.HIGH)
		effort.set(com.github.spotbugs.snom.Effort.MIN)
		reports.create("html") {
			enabled = true
		}
	}
}

tasks.compileJava {
	dependsOn("processResources")
	options.release.set(21)
	options.encoding = "UTF-8"
	options.compilerArgs.addAll(listOf("-Xlint:deprecation"))
}

tasks.processResources {
	val tokens = mapOf(
			"application.version" to project.version,
			"application.description" to project.description
	)
	filesMatching("**/*.yml") {
		filter<ReplaceTokens>("tokens" to tokens)
	}
}

tasks.test {
	failFast = false
	enableAssertions = true

	// Enable JUnit 5 (Gradle 4.6+).
	useJUnitPlatform()

	testLogging {
		events("PASSED", "STARTED", "FAILED", "SKIPPED")
		// Set to true if you want to see output from tests
		showStandardStreams = false
		setExceptionFormat("FULL")
	}

	systemProperty("io.netty.leakDetectionLevel", "paranoid")
}

defaultTasks("spotlessApply", "build")


kotlin {
	jvmToolchain(21)
}