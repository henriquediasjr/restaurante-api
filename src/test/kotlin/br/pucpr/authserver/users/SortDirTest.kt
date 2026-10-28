package br.pucpr.authserver.users

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class SortDirTest {
    @Test
    fun `findOrThrow throws InvalidSortDirException for invalid values`() {
        val exception = shouldThrow<InvalidSortDirException> {
            SortDir.findOrThrow("invalid")
        }
        exception shouldHaveMessage "Invalid sort direction! Use ASC or DESC"
    }

    @ParameterizedTest
    @ValueSource(strings = ["ASC", "asc", "AsC"])
    fun `findOrThrow finds ASC case insensitive`(dir: String) {
        SortDir.findOrThrow(dir) shouldBe SortDir.ASC
    }
}