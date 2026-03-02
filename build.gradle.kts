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
val junitVersion = "5.11.3"

sourceSets {
    create("integrationTest") {
        kotlin.srcDir("src/integrationTest/kotlin")
        compileClasspath += sourceSets.main.get().output
        runtimeClasspath += output + compileClasspath
    }
}

// Wire integrationTest configurations to extend test configurations
configurations["integrationTestImplementation"].extendsFrom(configurations.testImplementation.get())
configurations["integrationTestRuntimeOnly"].extendsFrom(configurations.testRuntimeOnly.get())

dependencies {
    implementation("io.modelcontextprotocol:kotlin-sdk-server:$mcpVersion")
    implementation("io.ktor:ktor-server-cio:$ktorVersion")
    implementation("io.ktor:ktor-server-sse:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:1.4.14")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2")
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
    useJUnitPlatform { excludeTags("integration") }
}

tasks.register<Test>("integrationTest") {
    group = "verification"
    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    useJUnitPlatform { includeTags("integration") }
    environment("OBSIDIAN_BOOKMARKS", "1")
    environment("OBSIDIAN_TEMPLATES", "1")
    environment("OBSIDIAN_PLUGINS", "1")
    environment("OBSIDIAN_THEMES", "1")
    environment("OBSIDIAN_SYNC", "1")
    environment("OBSIDIAN_PUBLISH", "1")
    environment("OBSIDIAN_BASES", "1")
    environment("OBSIDIAN_DEV_TOOLS", "1")
}
