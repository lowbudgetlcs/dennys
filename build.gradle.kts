import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

val kotlinVersion = "2.1.0"
val ktorVersion = "3.0.2"
val logbackVersion = "1.5.15"

plugins {
    kotlin("jvm") version ("2.1.0")
    id("application")
    id("org.jetbrains.kotlin.plugin.serialization") version ("2.1.0")
    id("com.gradleup.shadow") version "8.3.0"
    id("app.cash.sqldelight") version "2.0.2"
}

group = "com.lowbudgetlcs"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    google()
    mavenCentral()
}

tasks {
    "shadowJar"(ShadowJar::class) {
        mergeServiceFiles()
    }
}

sqldelight {
    databases {
        create("Database") {
            packageName.set("$group")
            dialect("app.cash.sqldelight:postgresql-dialect:2.0.2")
            srcDirs.setFrom("src/main/sqldelight")
            deriveSchemaFromMigrations.set(true)
        }
    }
}

dependencies {
    // Configuration
    implementation("com.sksamuel.hoplite:hoplite-core:2.9.0")
    implementation("com.sksamuel.hoplite:hoplite-yaml:2.9.0")
    // Ktor
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-request-validation:$ktorVersion")
    implementation("io.ktor:ktor-server-config-yaml-jvm:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    // Database
    implementation("app.cash.sqldelight:sqlite-driver:2.0.2")
    implementation("app.cash.sqldelight:jdbc-driver:2.0.2")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.postgresql:postgresql:42.7.2")
    // Rabbitmq
    implementation("com.rabbitmq:amqp-client:5.21.0")
    // Testing
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktorVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    testImplementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
}
