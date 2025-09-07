plugins {
    kotlin("jvm") version ("2.2.0")
    id("application")
    id("org.jetbrains.kotlin.plugin.serialization") version ("2.2.0")
    id("io.gitlab.arturbosch.detekt") version "1.23.6"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

group = "com.lowbudgetlcs"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
    mavenCentral()
    google()
}


tasks.register<Test>("generateJooq") {
    group = "codegen"
    description = "Generates Jooq data classes from Dennys database schema"
    testClassesDirs = sourceSets["jooq"].output.classesDirs
    classpath = sourceSets["jooq"].runtimeClasspath
}

tasks.register<Test>("itest") {
    group = "test"
    description = "Run integration source set"
    testClassesDirs = sourceSets["itest"].output.classesDirs
    classpath = sourceSets["itest"].runtimeClasspath
    shouldRunAfter("test")
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

sourceSets {
    val migrationsDir = "src/migrations"
    main {}
    test {}
    create("jooq") {
        resources.srcDir(migrationsDir)
    }
    create("itest") {
        resources.srcDir(migrationsDir)
        compileClasspath += sourceSets["main"].output
        runtimeClasspath += sourceSets["main"].output

    }
}

dependencies {
    implementation(libs.bundles.ktor.server)
    implementation(libs.bundles.ktor.server.plugins)
    implementation(libs.bundles.ktor.client)
    implementation(libs.bundles.ktor.client.plugins)
    implementation(libs.bundles.database)
    implementation(libs.bundles.server.logging)
    implementation(libs.hikari.core)
    implementation(libs.hoplite.core)

    // Jooq Code Generation
    "jooqImplementation"(libs.bundles.jooq.codegen)
    "jooqImplementation"(libs.bundles.kotest.integration)
    "jooqImplementation"(libs.postgresql.core)
    "jooqImplementation"(libs.logback.core)

    // Integration Testing
    "itestImplementation"(libs.bundles.kotest.integration)
    "itestImplementation"(libs.bundles.ktor.client)
    "itestImplementation"(libs.bundles.ktor.client.plugins)
    "itestImplementation"(libs.ktor.serialization.json)
    "itestImplementation"(libs.bundles.database)
    "itestImplementation"(libs.logback.core)
    "itestImplementation"(libs.hoplite.core)

    // Unit Testing
    testImplementation(libs.bundles.kotest.unit)
    testImplementation(libs.reflect.core)
}
