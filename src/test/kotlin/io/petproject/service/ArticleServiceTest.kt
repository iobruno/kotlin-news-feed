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
import java.time.format.DateTimeFormatter


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
                )
        )
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

    @Test
    fun `when searching by Authors, return a Page of Articles, of all matches`() {
        getArticles().map { service.publish(it) }
        val articles = service.search(
                authors = listOf("martin", "jetbrains"),
                tags = listOf(""),
                afterDate = null,
                beforeDate = null
        )
        assertThat(articles.size).isEqualTo(4)
    }

    @Test
    fun `when searching by Tags, return a Page of Articles, of all matches`() {
        getArticles().map { service.publish(it) }
        val articles = service.search(
                authors = listOf(),
                tags = listOf("algorithms", "kotlin", "scala"),
                afterDate = null,
                beforeDate = null
        )
        assertThat(articles.size).isEqualTo(3)
    }

    @Test
    fun `when searching by Date, return a Page of Articles, of all matches`() {
        getArticles().map { service.publish(it) }
        val articles = service.search(
                authors = listOf(),
                tags = listOf(),
                afterDate = LocalDate.of(2019, 1, 10),
                beforeDate = LocalDate.of(2019, 1, 16)
        )
        assertThat(articles.size).isEqualTo(2)
    }

    @Test
    fun `when searching by combining fields, return a Page of Articles, of all matches`() {
        getArticles().map { service.publish(it) }
        val articles = service.search(
                afterDate = LocalDate.of(2019, 1, 11),
                beforeDate = null,
                tags = listOf("performance", "algorithms", "kotlin"),
                authors = listOf("martin", "gayle")
        )
        assertThat(articles.size).isEqualTo(2)
    }

    @Test
    fun `when searching with authors and tags as String, result should be the same`() {
        getArticles().map { service.publish(it) }
        val articles = service.search(
                afterDate = LocalDate.of(2019, 1, 11),
                beforeDate = null,
                tags = "performance, algorithms, kotlin",
                authors = "martin, gayle"
        )
        assertThat(articles.size).isEqualTo(2)
    }

    private fun getArticles(): List<Article> {
        return listOf(
                Article("headline", "content", "summary", ArticleMetadata(
                        LocalDate.parse("2019-01-10T13:14:00", DateTimeFormatter.ISO_DATE_TIME),
                        listOf("scala", "functional-programming"),
                        listOf(Author("martin.odersky", "Martin Odersky"))
                )),
                Article("headline", "content", "summary", ArticleMetadata(
                        LocalDate.parse("2019-01-11T10:40:00", DateTimeFormatter.ISO_DATE_TIME),
                        listOf("microservices", "low-latency", "performance", "java"),
                        listOf(Author("martin.thompson", "Martin Thompson"))
                )),
                Article("headline", "content", "summary", ArticleMetadata(
                        LocalDate.parse("2019-01-12T10:40:00", DateTimeFormatter.ISO_DATE_TIME),
                        listOf("algorithms", "coding-interviews"),
                        listOf(Author("gayle", "Gayle McDowell"))
                )),
                Article("headline", "content", "summary", ArticleMetadata(
                        LocalDate.parse("2019-01-15T10:40:00", DateTimeFormatter.ISO_DATE_TIME),
                        listOf("kotlin", "microservices"),
                        listOf(Author("jetbrains", "JetBrains Press"))
                )),
                Article("headline", "content", "summary", ArticleMetadata(
                        LocalDate.parse("2019-01-16T10:40:00", DateTimeFormatter.ISO_DATE_TIME),
                        listOf("design-patterns", "java", "microservices"),
                        listOf(Author("martin.fowler", "Martin Fowler"))
                ))
        )
    }

}