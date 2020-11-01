package io.petproject.newsfeed.service

import io.petproject.newsfeed.Application
import io.petproject.newsfeed.model.Article
import io.petproject.newsfeed.model.Author
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
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
            .withHeadline("headline")
            .withContent("content")
            .wthSummary("summary")
            .withPubDate(LocalDate.now())
            .withTags(mutableListOf("first-tag", "second-tag"))
            .wthAuthors(mutableListOf(
                Author("john.doe", "John Doe"),
                Author("jane.doe", "Jane Doe")
            ))
            .build()
    }

    @Test
    fun `when publishing, if successful, it should return an article with an Id`() {
        val publishedArticle = service.publish(article)
        assertThat(publishedArticle.id).isPositive
    }

    @Test
    fun `when publishing articles with same authors, they should have the same authors id`() {
        val meta = article.meta
        val anotherArticle = Article.Builder()
            .withHeadline("anotherHeadline")
            .withContent("anotherContent")
            .wthSummary("anotherSummary")
            .withPubDate(meta.publishDate)
            .withTags(meta.tags)
            .wthAuthors(article.authors)
            .build()

        val publishedArticle = service.publish(article)
        val anotherPublishedArticle = service.publish(anotherArticle)
        assertThat(publishedArticle.authors.toList())
            .isEqualTo(anotherPublishedArticle.authors.toList())
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
            .withHeadline("New Headline")
            .withContent("New Content")
            .wthSummary(pubArticle.summary)
            .withPubDate(pubArticle.meta.publishDate)
            .withTags(pubArticle.meta.tags)
            .wthAuthors(authors)
            .build()

        val updatedArticle = service.update(pubArticle.id!!, updatingArticle)!!
        assertThat(updatedArticle.headline).isEqualTo(updatingArticle.headline)
        assertThat(updatedArticle.content).isEqualTo(updatingArticle.content)
        assertThat(updatedArticle.summary).isEqualTo(updatingArticle.summary)
        assertThat(updatedArticle.meta.publishDate).isEqualTo(updatingArticle.meta.publishDate)
        assertThat(updatedArticle.meta.tags).isEqualTo(updatingArticle.meta.tags)

        assertThat(updatedArticle.authors.map { it.username })
            .isEqualTo(authors.map { it.username })

        updatedArticle.authors.forEach {
            assertThat(it.id).isPositive()
        }
    }

    @Test
    fun `when updating an article, if it was not found, return null`() {
        val updatingArticle = Article.Builder()
            .withHeadline("New Headline")
            .withContent("New Content")
            .wthSummary("New Summary")
            .withPubDate(LocalDate.now())
            .withTags(mutableListOf("tag"))
            .wthAuthors(mutableListOf(Author("foo.bar", "Foobar")))
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
        assertThatThrownBy { service.purge(Long.MAX_VALUE) }
            .isInstanceOf(EmptyResultDataAccessException::class.java)
    }

    @Test
    fun `when searching by Authors, return a Page of Articles, of all matches`() {
        getArticles().map { service.publish(it) }
        val articles = service.search(
            listOf("gayle", "thompson"),
            listOf(),
            null,
            null
        )

        assertThat(articles.totalElements).isEqualTo(2)
    }

    @Test
    fun `when searching by Tags, return a Page of Articles, of all matches`() {
        getArticles().map { service.publish(it) }
        val articles = service.search(
            listOf(),
            listOf("algorithms", "kotlin", "scala"),
            null,
            null
        )

        assertThat(articles.totalElements).isEqualTo(3)
    }

    @Test
    fun `when searching by afterDate alone, return a Page of Articles, of all matches`() {
        getArticles().map { service.publish(it) }
        val articles = service.search(listOf(), listOf(), LocalDate.of(2019, 1, 11), null)
        assertThat(articles.totalElements).isEqualTo(4)
    }

    @Test
    fun `when searching by beforeDate alone, return a Page of Articles, of all matches`() {
        getArticles().map { service.publish(it) }
        val articles = service.search(listOf(), listOf(), null, LocalDate.of(2019, 1, 12))
        assertThat(articles.totalElements).isEqualTo(2)
    }

    @Test
    fun `when searching by afterDate and beforeDate, return a Page of Articles, of all matches`() {
        getArticles().map { service.publish(it) }
        val articles = service.search(
            listOf(),
            listOf(),
            LocalDate.of(2019, 1, 10),
            LocalDate.of(2019, 1, 16)
        )

        assertThat(articles.totalElements).isEqualTo(4)
    }

    @Test
    fun `when searching by combining fields, return a Page of Articles, of all matches`() {
        getArticles().map { service.publish(it) }
        val articles = service.search(
            listOf("martin", "gayle"),
            listOf("performance", "algorithms", "kotlin"),
            LocalDate.of(2019, 1, 11),
            null
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

    private fun getArticles() = mutableListOf(
        Article.builder
            .withId(1L)
            .withHeadline("headline I")
            .withContent("content")
            .wthSummary("summary")
            .wthAuthors(mutableListOf(Author("martin.odersky", "Martin Odersky")))
            .withPubDate(LocalDate.parse("2019-01-10T13:14:00", DateTimeFormatter.ISO_DATE_TIME))
            .withTags(mutableListOf("scala", "functional-programming"))
            .build(),
        Article.builder
            .withId(2L)
            .withHeadline("headline II")
            .withContent("content")
            .wthSummary("summary")
            .wthAuthors(mutableListOf(Author("martin.thompson", "Martin Thompson")))
            .withPubDate(LocalDate.parse("2019-01-11T10:40:00", DateTimeFormatter.ISO_DATE_TIME))
            .withTags(mutableListOf("microservices", "low-latency", "performance", "java"))
            .build(),
        Article.builder
            .withId(3L)
            .withHeadline("headline III")
            .withContent("content")
            .wthSummary("summary")
            .wthAuthors(mutableListOf(Author("gayle", "Gayle McDowell")))
            .withPubDate(LocalDate.parse("2019-01-12T10:40:00", DateTimeFormatter.ISO_DATE_TIME))
            .withTags(mutableListOf("algorithms", "coding-interviews"))
            .build(),
        Article.builder
            .withId(4L)
            .withHeadline("headline IV")
            .withContent("content")
            .wthSummary("summary")
            .wthAuthors(mutableListOf(Author("jetbrains", "JetBrains Press")))
            .withPubDate(LocalDate.parse("2019-01-15T10:40:00", DateTimeFormatter.ISO_DATE_TIME))
            .withTags(mutableListOf("kotlin", "microservices"))
            .build(),
        Article.builder
            .withId(5L)
            .withHeadline("headline V")
            .withContent("content")
            .wthSummary("summary")
            .wthAuthors(mutableListOf(Author("martin.fowler", "Martin Fowler")))
            .withPubDate(LocalDate.parse("2019-01-16T10:40:00", DateTimeFormatter.ISO_DATE_TIME))
            .withTags(mutableListOf("design-patterns", "java", "microservices"))
            .build()
    )
}
