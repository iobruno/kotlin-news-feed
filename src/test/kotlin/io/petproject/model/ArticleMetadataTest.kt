package io.petproject.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException
import java.time.LocalDate

internal class ArticleMetadataTest {

    @Test
    fun `when tags is empty, it should throw IllegalArgEx`() {
        assertThrows(IllegalArgumentException::class.java) {
            ArticleMetadata(publishDate = LocalDate.now(), tags = listOf(), authors = listOf(
                Author("john.doe", "John Doe"),
                Author("jane.doe", "Jane Doe")
            ))
        }
    }

    @Test
    fun `when authors is empty, it should throw IllegalArgEx`() {
        assertThrows(IllegalArgumentException::class.java) {
            ArticleMetadata(publishDate = LocalDate.now(), authors = listOf(), tags = listOf("cassandra", "aws"))
        }
    }

}