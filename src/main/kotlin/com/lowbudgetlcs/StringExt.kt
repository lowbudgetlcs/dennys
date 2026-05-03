package com.lowbudgetlcs

import com.sksamuel.hoplite.Masked

fun String.byteify() = this.toByteArray(Charsets.UTF_8)
fun String.toMasked(): Masked = Masked(this)
