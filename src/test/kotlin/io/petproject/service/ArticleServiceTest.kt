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
    fun setup() {
        article = Article.Builder()
                .headline("headline")
                .content("content")
                .summary("summary")
                .publishDate(LocalDate.now())
                .tags(mutableListOf("first-tag", "second-tag"))
                .authors(mutableListOf(
                        Author("john.doe", "John Doe"),
                        Author("jane.doe", "Jane Doe")
                ))
                .build()
    }

    @Test
    fun `when publishing, if successful, it should return an article with an Id`() {
        val publishedArticle = service.publish(article)
        assertThat(publishedArticle.id).isPositive()
    }

    @Test
    fun `when publishing articles with same authors, they should have the same authors id`() {
        val meta = article.meta
        val anotherArticle = Article.Builder()
                .headline("anotherHeadline")
                .content("anotherContent")
                .summary("anotherSummary")
                .publishDate(meta.publishDate)
                .tags(meta.tags)
                .authors(meta.authors)
                .build()

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
    fun `when updating an article, if it was found, update and return it`() {
        val pubArticle = service.publish(article)
        val authors = mutableListOf(
                Author("john.smith", "John Smith"),
                Author("john.doe", "John Doe")
        )
        val updatingArticle = Article.Builder()
                .headline("New Headline")
                .content("New Content")
                .summary(pubArticle.summary)
                .publishDate(pubArticle.meta.publishDate)
                .tags(pubArticle.meta.tags)
                .authors(authors)
                .build()

        val updatedArticle = service.update(pubArticle.id!!, updatingArticle)!!
        assertThat(updatedArticle.headline).isEqualTo(updatingArticle.headline)
        assertThat(updatedArticle.content).isEqualTo(updatingArticle.content)
        assertThat(updatedArticle.summary).isEqualTo(updatingArticle.summary)
        assertThat(updatedArticle.meta.publishDate).isEqualTo(updatingArticle.meta.publishDate)
        assertThat(updatedArticle.meta.tags).isEqualTo(updatingArticle.meta.tags)

        assertThat(updatedArticle.meta.authors.map { it.username })
                .isEqualTo(authors.map { it.username })

        updatedArticle.meta.authors.forEach {
            assertThat(it.id).isPositive()
        }
    }

    @Test
    fun `when updating an article, if it was not found, return null`() {
        val updatingArticle = Article.Builder()
                .headline("New Headline")
                .content("New Content")
                .summary("New Summary")
                .publishDate(LocalDate.now())
                .tags(mutableListOf("tag"))
                .authors(mutableListOf(Author("foo.bar", "Foobar")))
                .build()

        val updatedArticle = service.update(Long.MAX_VALUE, updatingArticle)
        assertThat(updatedArticle).isNull()
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
                authors = listOf("gayle", "thompson"),
                tags = listOf(),
                afterDate = null,
                beforeDate = null
        )
        assertThat(articles.totalElements).isEqualTo(2)
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
        assertThat(articles.totalElements).isEqualTo(3)
    }

    @Test
    fun `when searching by afterDate alone, return a Page of Articles, of all matches`() {
        getArticles().map { service.publish(it) }
        val articles = service.search(
                authors = listOf(),
                tags = listOf(),
                afterDate = LocalDate.of(2019, 1, 11),
                beforeDate = null
        )
        assertThat(articles.totalElements).isEqualTo(4)
    }

    @Test
    fun `when searching by beforeDate alone, return a Page of Articles, of all matches`() {
        getArticles().map { service.publish(it) }
        val articles = service.search(
                authors = listOf(),
                tags = listOf(),
                afterDate = null,
                beforeDate = LocalDate.of(2019, 1, 12)
        )
        assertThat(articles.totalElements).isEqualTo(2)
    }

    @Test
    fun `when searching by afterDate and beforeDate, return a Page of Articles, of all matches`() {
        getArticles().map { service.publish(it) }
        val articles = service.search(
                authors = listOf(),
                tags = listOf(),
                afterDate = LocalDate.of(2019, 1, 10),
                beforeDate = LocalDate.of(2019, 1, 16)
        )
        assertThat(articles.totalElements).isEqualTo(4)
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
        assertThat(articles.totalElements).isEqualTo(2)
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
        assertThat(articles.totalElements).isEqualTo(2)
    }

    private fun getArticles(): List<Article> {
        return mutableListOf(
                Article(headline = "headline", content = "content", summary = "summary", id = 1L,
                        meta = ArticleMetadata(
                                publishDate = LocalDate.parse("2019-01-10T13:14:00", DateTimeFormatter.ISO_DATE_TIME),
                                tags = mutableListOf("scala", "functional-programming"),
                                authors = mutableListOf(Author("martin.odersky", "Martin Odersky")))
                ),
                Article(headline = "headline", content = "content", summary = "summary", id = 2L,
                        meta = ArticleMetadata(
                                publishDate = LocalDate.parse("2019-01-11T10:40:00", DateTimeFormatter.ISO_DATE_TIME),
                                tags = mutableListOf("microservices", "low-latency", "performance", "java"),
                                authors = mutableListOf(Author("martin.thompson", "Martin Thompson")))
                ),
                Article(headline = "headline", content = "content", summary = "summary", id = 3L,
                        meta = ArticleMetadata(
                                publishDate = LocalDate.parse("2019-01-12T10:40:00", DateTimeFormatter.ISO_DATE_TIME),
                                tags = mutableListOf("algorithms", "coding-interviews"),
                                authors = mutableListOf(Author("gayle", "Gayle McDowell")))
                ),
                Article(headline = "headline", content = "content", summary = "summary", id = 4L,
                        meta = ArticleMetadata(
                                publishDate = LocalDate.parse("2019-01-15T10:40:00", DateTimeFormatter.ISO_DATE_TIME),
                                tags = mutableListOf("kotlin", "microservices"),
                                authors = mutableListOf(Author("jetbrains", "JetBrains Press")))
                ),
                Article(headline = "headline", content = "content", summary = "summary", id = 5L,
                        meta = ArticleMetadata(
                                publishDate = LocalDate.parse("2019-01-16T10:40:00", DateTimeFormatter.ISO_DATE_TIME),
                                tags = mutableListOf("design-patterns", "java", "microservices"),
                                authors = mutableListOf(Author("martin.fowler", "Martin Fowler")))
                )
        )
    }

}