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
                .headline("headline")
                .content(" ")
                .summary("summary")
                .authors(authors.toMutableList())
                .publishDate(metadata.publishDate)
                .tags(metadata.tags)
                .build()
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Content must not be blank")
    }

    @Test
    fun `when authors is empty, it should throw IllegalArgEx`() {
        assertThatThrownBy {
            Article.builder
                .headline("headline")
                .content("content")
                .summary("summary")
                .authors(mutableListOf())
                .publishDate(metadata.publishDate)
                .tags(metadata.tags)
                .build()
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("There must be at least one author for the article")
    }
}
