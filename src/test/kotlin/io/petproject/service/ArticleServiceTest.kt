package io.petproject.service

import io.petproject.Application
import io.petproject.model.Article
import io.petproject.model.ArticleMetadata
import io.petproject.model.Author
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
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

}