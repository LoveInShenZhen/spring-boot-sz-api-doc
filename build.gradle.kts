import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.72"
    id("maven-publish")
}

val springBootVersion = "2.2.7.RELEASE"
val springFrameworkVersion = "5.2.6.RELEASE"
val jacksonVersion = "2.10.4"

group = "io.github.loveinshenzhen"
version = "1.0.0"
java.sourceCompatibility = JavaVersion.VERSION_1_8


repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-freemarker:$springBootVersion")
    implementation("org.springframework:spring-webmvc:$springFrameworkVersion")
    implementation("org.springframework:spring-webflux:$springFrameworkVersion")

    implementation(kotlin("reflect"))
    implementation(kotlin("stdlib-jdk8"))

    implementation("com.fasterxml.jackson.module:jackson-module-jsonSchema:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")

    api("org.jodd:jodd-core:5.1.3")
    api("org.apache.commons:commons-lang3:3.9")

}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks.register<Jar>("sourcesJar") {
    from(sourceSets.main.get().allSource)
    archiveClassifier.set("sources")
}

publishing {
    publications {
        create<MavenPublication>("maven") {

            from(components["java"])
            artifact(tasks["sourcesJar"])
        }
    }
}