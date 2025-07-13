val kotlinVersion = "2.2.0"
val ktorVersion = "3.2.1"
val logbackVersion = "1.5.18"
val jooqVersion = "3.19.18"
val kotestVersion = "5.9.0"
val testcontainersVersion = "1.21.3"
val postgresDriverVersion = "42.7.7"


plugins {
    kotlin("jvm") version ("2.2.0")
    id("application")
    id("org.jetbrains.kotlin.plugin.serialization") version ("2.2.0")
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
    implementation("com.sksamuel.hoplite:hoplite-core:2.9.0")
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-request-validation:$ktorVersion")
    implementation("io.ktor:ktor-server-config-yaml-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:$ktorVersion")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("org.postgresql:postgresql:$postgresDriverVersion")
    implementation("org.jooq:jooq:$jooqVersion")

    // Jooq Code Generation
    "jooqImplementation"("io.kotest:kotest-runner-junit5:$kotestVersion")
    "jooqImplementation"("io.kotest.extensions:kotest-extensions-testcontainers:2.0.2")
    "jooqImplementation"("org.testcontainers:postgresql:$testcontainersVersion")
    "jooqImplementation"("org.jooq:jooq-meta:$jooqVersion")
    "jooqImplementation"("org.jooq:jooq-codegen:$jooqVersion")
    "jooqImplementation"("org.postgresql:postgresql:$postgresDriverVersion")
    "jooqImplementation"("ch.qos.logback:logback-classic:$logbackVersion")

    // Integration Testing
    "itestImplementation"("io.kotest:kotest-runner-junit5:$kotestVersion")
    "itestImplementation"("io.kotest.extensions:kotest-extensions-testcontainers:2.0.2")
    "itestImplementation"("org.testcontainers:testcontainers:$testcontainersVersion")
    "itestImplementation"("org.testcontainers:postgresql:$testcontainersVersion")
    "itestImplementation"("org.jooq:jooq:$jooqVersion")
    "itestImplementation"("org.postgresql:postgresql:$postgresDriverVersion")
    "itestImplementation"("ch.qos.logback:logback-classic:$logbackVersion")

    // Unit Testing
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktorVersion")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    testImplementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    testImplementation("io.ktor:ktor-client-mock:$ktorVersion")
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation(kotlin("reflect"))

}