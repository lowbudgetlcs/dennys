plugins {
    id("application")
    kotlin("jvm")
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation("ch.qos.logback:logback-classic:1.5.18")
}

sourceSets {
    create("codegen") {
        resources.srcDir("src/migrations")
    }
    test {
        resources.srcDir("src/migrations")
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}