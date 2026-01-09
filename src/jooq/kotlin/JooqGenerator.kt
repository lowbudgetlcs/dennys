package com.example.jooq

import org.jooq.codegen.GenerationTool
import org.jooq.meta.jaxb.Configuration
import org.jooq.meta.jaxb.Database
import org.jooq.meta.jaxb.ForcedType
import org.jooq.meta.jaxb.Generator
import org.jooq.meta.jaxb.Jdbc
import org.jooq.meta.jaxb.Property
import org.jooq.meta.jaxb.Target

fun main() {
    val migrationsDir = System.getProperty("sql.migrations.dir")
    val config = Configuration().apply {
        generator = Generator().apply {
            name = "org.jooq.codegen.KotlinGenerator"
            database = Database().apply {
                jdbc = Jdbc().apply {
                    driver = "org.postgresql.Driver"
                    url = ""
                    username = ""
                    password = ""
                }
                name = "org.jooq.meta.extensions.ddl.DDLDatabase"
                includes = ".*"
                forcedTypes = listOf(
                    ForcedType().apply {
                        name = "INSTANT"
                        includeTypes = "TIMESTAMPTZ"
                    },
                    ForcedType().apply {
                        name = "INSTANT"
                        includeTypes = "TIMESTAMP\\ WITH\\ TIME\\ ZONE"
                    },
                )
                inputSchema = "dennys"
                properties = listOf(
                    Property().apply {
                        key = "scripts"
                        value = migrationsDir
                    },
                    Property().apply {
                        key = "defaultNameCase"
                        value = "lower"
                    },
                    Property().apply {
                        key = "dialect"
                        value = "POSTGRES"
                    },
                    Property().apply {
                        key = "execute"
                        value = "false"
                    },
                )
            }
            target = Target().apply {
                packageName = System.getProperty("jooq.package")
                directory = System.getProperty("jooq.directory")
            }
        }
    }
    GenerationTool.generate(config)
}
