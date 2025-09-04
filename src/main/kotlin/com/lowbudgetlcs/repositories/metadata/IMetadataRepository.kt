package com.lowbudgetlcs.repositories.metadata

interface IMetadataRepository {
    fun getProviderId(): Int?
    fun setProviderId(id: Int): Int?
}