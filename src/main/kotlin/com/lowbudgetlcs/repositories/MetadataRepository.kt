package com.lowbudgetlcs.repositories

import org.jooq.DSLContext
import org.jooq.storage.tables.references.METADATA

class MetadataRepository(private val dsl: DSLContext) : IMetadataRepository {
    override fun init(providerId: Int, bucketName: String): Boolean = try {
        dsl.insertInto(METADATA).set(METADATA.RIOT_PROVIDER_ID, providerId).set(METADATA.LOGO_BUCKET_NAME, bucketName)
            .execute()
        true
    } catch (e: Throwable) {
        false
    }

    override fun getProviderId(): Int? {
        val metadata = dsl.select(METADATA.RIOT_PROVIDER_ID).from(METADATA).fetchOne()
        return metadata?.get(METADATA.RIOT_PROVIDER_ID)
    }

    override fun setProviderId(id: Int): Int? =
        dsl.update(METADATA).set(METADATA.RIOT_PROVIDER_ID, id).returning(METADATA.RIOT_PROVIDER_ID).fetchOne()
            ?.get(METADATA.RIOT_PROVIDER_ID)
}
