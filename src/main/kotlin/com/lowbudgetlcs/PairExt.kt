package com.lowbudgetlcs

fun <T, E> Pair<T, E>.equalsIgnoreOrder(other: Pair<T, E>) = this.toList().toSet() == other.toList().toSet()
