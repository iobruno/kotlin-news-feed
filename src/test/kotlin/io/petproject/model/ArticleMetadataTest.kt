package io.petproject.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.IllegalArgumentException
import java.time.LocalDate

internal class ArticleMetadataTest {

    @Test
    fun `when tags is empty, it should throw IllegalArgEx`() {
        assertThrows<IllegalArgumentException> {
            ArticleMetadata(
                    publishDate = LocalDate.now(),
                    tags = mutableListOf(),
                    authors = mutableListOf(
                            Author("john.doe", "John Doe"),
                            Author("jane.doe", "Jane Doe"))
            )
        }
    }

    @Test
    fun `when authors is empty, it should throw IllegalArgEx`() {
        assertThrows<IllegalArgumentException> {
            ArticleMetadata(
                    publishDate = LocalDate.now(),
                    authors = mutableListOf(),
                    tags = mutableListOf("cassandra", "aws"))
        }
    }

}