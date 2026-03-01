plugins {
    kotlin("jvm") version "2.3.10"
    kotlin("plugin.serialization") version "2.3.10"
    id("com.gradleup.shadow") version "8.3.9"
    application
}


group = "tech.codingzen"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val mcpVersion = "0.8.4"
val ktorVersion = "3.2.3"

dependencies {
    implementation("io.modelcontextprotocol:kotlin-sdk-server:$mcpVersion")
    implementation("io.ktor:ktor-server-cio:$ktorVersion")
    implementation("io.ktor:ktor-server-sse:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:1.4.14")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("tech.codingzen.obsidian.MainKt")
}

tasks.shadowJar {
    archiveBaseName.set("my-mcp-server")
    archiveClassifier.set("")
    archiveVersion.set("")
}

tasks.test {
    useJUnitPlatform()
}