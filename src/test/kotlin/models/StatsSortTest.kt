package models

import com.lowbudgetlcs.domain.InvalidRequestException
import com.lowbudgetlcs.domain.stats.models.StatsSort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class StatsSortTest :
    StringSpec({
        "a stat with a direction parses, and a missing direction means descending" {
            StatsSort.parse("kills:asc") shouldBe StatsSort("kills", descending = false)
            StatsSort.parse("kills:desc") shouldBe StatsSort("kills", descending = true)
            StatsSort.parse("killsPerGame") shouldBe StatsSort("killsPerGame", descending = true)
        }

        "a malformed sort is an invalid request" {
            listOf("", ":desc", "kills:sideways", "kills:desc:asc").forEach {
                shouldThrow<InvalidRequestException> { StatsSort.parse(it) }
            }
        }
    })
