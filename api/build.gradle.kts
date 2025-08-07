plugins {
    id("kotlin-common")
    id("org.jetbrains.kotlin.plugin.serialization") version ("2.2.0")
}

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

tasks.named("compileKotlin") {
    dependsOn(":jooq:generateJooq")
}

dependencies {
    implementation(libs.bundles.ktor.server)
    implementation(libs.bundles.ktor.server.plugins)
    implementation(libs.bundles.ktor.client)
    implementation(libs.bundles.ktor.client.plugins)
    implementation(libs.bundles.database)
    implementation(libs.hikari.core)
    implementation(libs.hoplite.core)
    implementation(project(":jooq"))

    // Unit + Integration Testing
    testImplementation(libs.bundles.kotest.unit)
    testImplementation(libs.reflect.core)
    testImplementation(libs.bundles.kotest.integration)
    testImplementation(libs.bundles.database)
}
