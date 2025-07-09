val kotlinVersion = "2.2.0"
val ktorVersion = "3.1.3"
val logbackVersion = "1.5.18"
val jooqVersion = "3.19.8"
val kotestVersion = "5.9.0"
val junitVersion = ""
val testcontainerVerison = ""


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

val jooqImplementation by configurations.creating {
    extendsFrom(configurations.implementation.get())
}

val itestImplementation by configurations.creating {
    extendsFrom(configurations.implementation.get())
}

dependencies {
    implementation("com.sksamuel.hoplite:hoplite-core:2.9.0")
    implementation("io.ktor:ktor-server-core-jvm:${ktorVersion}")
    implementation("io.ktor:ktor-server-netty-jvm:${ktorVersion}")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:${ktorVersion}")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:${ktorVersion}")
    implementation("io.ktor:ktor-server-request-validation:${ktorVersion}")
    implementation("io.ktor:ktor-server-config-yaml-jvm:${ktorVersion}")
    implementation("io.ktor:ktor-server-status-pages:${ktorVersion}")
    implementation("io.ktor:ktor-client-core:${ktorVersion}")
    implementation("io.ktor:ktor-client-cio:${ktorVersion}")
    implementation("io.ktor:ktor-client-content-negotiation:${ktorVersion}")
    implementation("ch.qos.logback:logback-classic:${logbackVersion}")
    implementation("com.zaxxer:HikariCP:5.1.0")

    // Database
    implementation("org.postgresql:postgresql:42.7.2")
    implementation("org.jooq:jooq:$jooqVersion")

    // Jooq Code Generation
    jooqImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    jooqImplementation("org.testcontainers:junit-jupiter:1.21.3")
    jooqImplementation("org.testcontainers:postgresql:1.21.3")
    jooqImplementation("org.jooq:jooq-meta:${jooqVersion}")
    jooqImplementation("org.jooq:jooq-codegen:${jooqVersion}")

    // Integration Testing
    itestImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    itestImplementation("io.kotest.extensions:kotest-extensions-testcontainers:2.0.2")
    itestImplementation("org.testcontainers:testcontainers:1.21.3")
    itestImplementation("org.testcontainers:postgresql:1.21.3")

    // Unit Testing
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktorVersion")
    testImplementation("io.kotest:kotest-runner-junit5:5.5.5")
    testImplementation("io.kotest:kotest-assertions-core:5.5.5")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    testImplementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    testImplementation("io.ktor:ktor-client-mock:$ktorVersion")
    testImplementation("io.mockk:mockk:1.13.5")
}

tasks.register<Test>("generateJooq") {
    group = "jooq"
    description = "Generates Jooq data classes from Dennys database schema"
    testClassesDirs = sourceSets["jooq"].output.classesDirs
    classpath = sourceSets["jooq"].runtimeClasspath
}

tasks.register<Test>("itests") {
    group = "test"
    description = "Run integration source set"
    testClassesDirs = sourceSets["itest"].output.classesDirs
    classpath = sourceSets["itest"].runtimeClasspath
}

tasks.withType<Test>().configureEach(){
    useJUnitPlatform()
}

sourceSets {
    main {}
    test {}
    create("itest") {
        compileClasspath += sourceSets["main"].output
        runtimeClasspath += sourceSets["main"].output
    }
    create("jooq") {}
}
