package io.petproject.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException
import java.time.LocalDate

internal class ArticleTest {

    private lateinit var metadata: ArticleMetadata

    @BeforeEach
    fun setup() {
        metadata = ArticleMetadata(
            publishDate = LocalDate.now(),
            tags = listOf("first-tag", "second-tag"),
            authors = listOf(
                Author("john.doe", "John Doe"),
                Author("jane.doe", "Jane Doe")
            )
        )
    }

    @Test
    fun `when headline is blank, it should throw IllegalArgEx`() {
        assertThrows(IllegalArgumentException::class.java) {
            Article(" ", "content", null, metadata)
        }
    }

    @Test
    fun `when content is blank, it should throw IllegalArgEx`() {
        assertThrows(IllegalArgumentException::class.java) {
            Article("headline", " ", null, metadata)
        }
    }

}