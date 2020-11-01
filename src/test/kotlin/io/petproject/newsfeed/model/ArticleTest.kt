package io.petproject.newsfeed.model

import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class ArticleTest {

    private val metadata =
        ArticleMetadata(LocalDate.now(), mutableListOf("first-tag", "second-tag"))

    private val authors = listOf(
        Author("john.doe", "John Doe"),
        Author("jane.doe", "Jane Doe")
    )

    @Test
    fun `when headline is blank, it should throw IllegalArgEx`() {
        assertThatThrownBy { Article(" ", "content", "summary", authors.toMutableList(), metadata) }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Headline must not be blank")
    }

    @Test
    fun `when content is blank, it should throw IllegalArgEx`() {
        assertThatThrownBy {
            Article.builder
                .withHeadline("headline")
                .withContent(" ")
                .wthSummary("summary")
                .wthAuthors(authors.toMutableList())
                .withPubDate(metadata.publishDate)
                .withTags(metadata.tags)
                .build()
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Content must not be blank")
    }

    @Test
    fun `when authors is empty, it should throw IllegalArgEx`() {
        assertThatThrownBy {
            Article.builder
                .withHeadline("headline")
                .withContent("content")
                .wthSummary("summary")
                .wthAuthors(mutableListOf())
                .withPubDate(metadata.publishDate)
                .withTags(metadata.tags)
                .build()
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("There must be at least one author for the article")
    }
}
