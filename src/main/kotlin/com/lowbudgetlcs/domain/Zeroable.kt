package com.lowbudgetlcs.domain

/**
 * This container class is used to represent values that can be set, null, or 'zero'.
 *
 * Example:
 * We have a patch class where every field is nullable. This is to allow fluent patching,
 * where we can only set the fields we care about. Now suppose one of the values can genuinely be null.
 * If you pass a null, it will be ignored because all the values are null by default. Enter, Zeroable<T>().
 * If you pass Zeroable(null), this implies 'null'.
 * If you pass Zeroable("Hello"), this implies '"Hello"'.
 * If you pass Zeroable(ANYTHING, true), this implies 'Ignore this field- it is 'zero'.
 *
 * The idea for this class came from the EventUpdate class, because one of the fields was genuinely nullable.
 * It is a good idea to use mappers to abstract this away from clients.
 */
class Zeroable<T>(
    val value: T?,
    val isZero: Boolean = false,
)
