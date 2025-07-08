import org.gradle.kotlin.dsl.provideDelegate

val kotlinVersion = "2.2.0"
val ktorVersion = "3.1.3"
val logbackVersion = "1.5.18"
val dbUser: String by project
val dbPassword: String by project


plugins {
    kotlin("jvm") version ("2.2.0")
    id("application")
    id("org.jetbrains.kotlin.plugin.serialization") version ("2.2.0")
    id("org.jooq.jooq-codegen-gradle") version ("3.19.8")
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

dependencies {
    // Configuration
    implementation("com.sksamuel.hoplite:hoplite-core:2.9.0")

    // Ktor
    implementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-request-validation:$ktorVersion")
    implementation("io.ktor:ktor-server-config-yaml-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-status-pages:${ktorVersion}")
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    // Database
    implementation("com.zaxxer:HikariCP:5.1.0")
    jooqCodegen("org.postgresql:postgresql:42.7.2")
    implementation("org.jooq:jooq:3.19.8")

    // Testing
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktorVersion")
    testImplementation("io.kotest:kotest-runner-junit5:5.5.5")
    testImplementation("io.kotest:kotest-assertions-core:5.5.5")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
    testImplementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    testImplementation("io.ktor:ktor-client-mock:$ktorVersion")
    testImplementation("io.mockk:mockk:1.13.5")
}

val jooqOutputDir = "build/generated-src/jooq/main"
jooq {
    version = "3.19.8"
   configuration {
       jdbc {
           driver = "org.postgresql.Driver"
           url = "jdbc:postgresql://127.0.0.1:5432/postgres"
           user = dbUser
           password = dbPassword
       }
       generator {
           name = "org.jooq.codegen.KotlinGenerator"
           database {
               name = "org.jooq.meta.postgres.PostgresDatabase"
               includes = ".*"
               inputSchema = "dennys"
           }
           generate {}
           target {
               packageName = "lblcs"
               directory = jooqOutputDir
           }
       }
   }
}

tasks.register<Exec>("dbStart") {
    workingDir = projectDir
    commandLine = listOf("docker", "compose", "up", "-d", "postgres")
    standardOutput = System.out
    errorOutput = System.err
}

tasks.register<Exec>("dbStop") {
    workingDir = projectDir
    commandLine = listOf("docker", "compose", "down", "postgres")
    standardOutput = System.out
    errorOutput = System.err
}

tasks.register<Copy>("copyJooqCode") {
    dependsOn("jooqCodegen")
    from("build/generated-src/jooq/main")
    into("src/main/jooq")
}

tasks.named("copyJooqCode") {
    mustRunAfter("dbStart")
}

tasks.register("generateJooqFromContainerDb") {
    dependsOn("dbStart", "copyJooqCode")
    finalizedBy("dbStop")
}

sourceSets {
    main {
        kotlin {
            srcDir("src/main/jooq")
        }
    }
}