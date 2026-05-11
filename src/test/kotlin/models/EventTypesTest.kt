package models

import com.lowbudgetlcs.domain.event.models.toEventName
import com.lowbudgetlcs.domain.event.models.types.EVENT_NAME_MAX_LENGTH
import com.lowbudgetlcs.domain.event.models.types.EVENT_NAME_MIN_LENGTH
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class EventTypesTest : StringSpec({
    "EventName cannot be empty" {
        shouldThrow<IllegalArgumentException> {
            "".toEventName()
        }
    }

    "EventName cannot exceed $EVENT_NAME_MAX_LENGTH characters" {
        shouldThrow<IllegalArgumentException> {
            "1".repeat(EVENT_NAME_MAX_LENGTH + 1).toEventName()
        }
    }

    "EventName must be at least $EVENT_NAME_MIN_LENGTH characters" {
        shouldThrow<IllegalArgumentException> {
            "1".repeat(EVENT_NAME_MIN_LENGTH - 1).toEventName()
        }
    }

    "Valid EventName does not throw" {
        shouldNotThrow<IllegalArgumentException> {
            "1".repeat(EVENT_NAME_MAX_LENGTH).toEventName()
            "1".repeat(EVENT_NAME_MIN_LENGTH).toEventName()
            "Season 16 Financial".toEventName()
        }
        val event = "Season 16 Financial".toEventName()
        event.value shouldBe "Season 16 Financial"
    }
})
