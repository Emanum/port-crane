import org.jetbrains.kotlin.ir.backend.js.compile

val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val kubernetes_version: String by project

plugins {
    kotlin("jvm") version "1.8.20"
    id("io.ktor.plugin") version "2.2.4"
}

group = "dev.emanum"
version = "0.0.1"
application {
    mainClass.set("dev.emanum.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-auth-jvm:$ktor_version")
    implementation("io.ktor:ktor-server-netty-jvm:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")
    implementation("io.ktor:ktor-server-content-negotiation:$ktor_version")
//    implementation("io.ktor:ktor-server-openapi:$ktor_version")
//    implementation("io.ktor:ktor-server-swagger:$ktor_version")
//    implementation("org.yaml:snakeyaml:1.33")


    implementation("io.kubernetes:client-java-api:$kubernetes_version")
    implementation("io.kubernetes:client-java:$kubernetes_version")
    implementation("io.kubernetes:client-java-extended:$kubernetes_version")
    implementation("io.kubernetes:client-java-proto:$kubernetes_version")
    implementation("io.ktor:ktor-server-cors-jvm:2.2.4")


    testImplementation("io.ktor:ktor-server-tests-jvm:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}