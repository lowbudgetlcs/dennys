package com.lowbudgetlcs.repositories

interface IMetadataRepository {
    fun init(providerId: Int, bucketName: String): Boolean
    fun getProviderId(): Int?
    fun setProviderId(id: Int): Int?
}