package com.lowbudgetlcs.repositories

import org.jooq.DSLContext
import org.jooq.storage.tables.references.METADATA

class MetadataRepository(private val dsl: DSLContext) : IMetadataRepository {
    override fun getProviderId(): Int? {
        val metadata = dsl.select(METADATA.RIOT_PROVIDER_ID).from(METADATA).fetchOne()
        return metadata?.get(METADATA.RIOT_PROVIDER_ID)
    }

}