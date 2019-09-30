package io.petproject.service

import io.petproject.Application
import io.petproject.model.Article
import io.petproject.model.ArticleMetadata
import io.petproject.model.Author
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate

@ExtendWith(SpringExtension::class)
@DataJpaTest
@ContextConfiguration(classes = [
    ArticleService::class,
    Application::class
])
internal class ArticleServiceTest {

    @Autowired
    private lateinit var service: ArticleService
    private lateinit var article: Article

    @BeforeEach
    fun `setup`() {
        article = Article (
                headline = "headline",
                content = "content",
                summary = "summary",
                meta = ArticleMetadata(
                        publishDate = LocalDate.now(),
                        tags = listOf("first-tag", "second-tag"),
                        authors = listOf(
                                Author("john.doe", "John Doe"),
                                Author("jane.doe", "Jane Doe")
                        )
                ))
    }

    @Test
    fun `when publishing, if successful, it should return an article with an Id`() {
        val publishedArticle = service.publish(article)
        assertThat(publishedArticle.id).isPositive()
    }

    @Test
    fun `when publishing articles with same authors, they should have the same authors id`() {
        val anotherArticle = Article(
                "anotherHeadline",
                "anotherContent",
                "anotherSummary",
                article.meta
        )

        val publishedArticle = service.publish(article)
        val anotherPublishedArticle = service.publish(anotherArticle)

        assertThat(publishedArticle.meta.authors.toList())
                .isEqualTo(anotherPublishedArticle.meta.authors.toList())
    }

    @Test
    fun `when retrieving an article, if it was found, return the object`() {
        val publishedArticle = service.publish(article)
        val article: Article? = service.retrieve(publishedArticle.id!!)
        assertThat(article).isNotNull
    }

    @Test
    fun `when retrieving an article, if it was not found, return null`() {
        val article: Article? = service.retrieve(Long.MAX_VALUE)
        assertThat(article).isNull()
    }

    @Test
    fun `when purging an article, if it was found, delete it`() {
        val publishedArticle = service.publish(article)
        service.purge(publishedArticle.id!!)
        assertThat(service.retrieve(publishedArticle.id!!)).isNull()
    }

    @Test
    fun `when purging an article, if it was not found, throw EmptyResultDataAccessEx`() {
        assertThrows(EmptyResultDataAccessException::class.java) {
            service.purge(Long.MAX_VALUE)
        }
    }

}