package com.lowbudgetlcs.domain

sealed interface PatchField<out T> {
    /** Field was not provided -> ignore */
    data object Unset : PatchField<Nothing>

    /** Field was provided (including null) */
    data class Value<T>(
        val value: T,
    ) : PatchField<T>
}
