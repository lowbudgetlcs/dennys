plugins {
    id("kotlin-common")
}
sourceSets {
    codegen {
        kotlin.srcDir("src/codegen/kotlin")
        resources.srcDir("src/codegen/resources")
        compileClasspath += sourceSets["main"].compileClasspath
        runtimeClasspath += sourceSets["main"].runtimeClasspath
        dependencies {
            implementation(libs.bundles.jooq.codegen)
            implementation(libs.testcontainers.core)
            implementation(libs.testcontainers.postgres)
            implementation(libs.postgresql.core)
        }
    }
}

val generateJooq by tasks.registering(JavaExec::class) {
    group = "codegen"
    description = "Generates jOOQ classes using Testcontainers and a running DB"

    classpath = sourceSets["codegen"].runtimeClasspath
    mainClass.set("JooqCodegenRunnerKt")
    doFirst {
        file("src/main/kotlin").mkdirs()
    }
    dependsOn(":migrations:processResources")
}