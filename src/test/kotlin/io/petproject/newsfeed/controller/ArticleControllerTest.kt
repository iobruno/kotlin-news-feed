package io.petproject.newsfeed.controller

import io.petproject.newsfeed.model.Article
import io.petproject.newsfeed.model.ArticleMetadata
import io.petproject.newsfeed.model.Author
import io.petproject.newsfeed.service.ArticleService
import io.restassured.RestAssured
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.domain.PageImpl
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.test.web.servlet.MockMvc
import java.time.LocalDate
import java.time.format.DateTimeFormatter.ISO_DATE_TIME

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
internal class ArticleControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var service: ArticleService

    @LocalServerPort
    private var serverPort: Int = 0

    @BeforeEach
    fun setup() {
        RestAssured.port = serverPort
    }

    private val authors = mutableListOf(
        Author("john.doe", "John Doe", 1L),
        Author("jane.doe", "Jane Doe", 2L)
    )

    private val metadata =
        ArticleMetadata(LocalDate.now(), mutableListOf("first-tag", "second-tag"))

    companion object {
        const val BASE_URL = "/api/v1/articles"
    }

    private fun <T> any(): T {
        return Mockito.any<T>()
    }

    @Test
    fun `when publishing an article, if it was successful, return status 201`() {
        `when`(service.publish(any()))
            .thenReturn(Article("headline", "content", "summary", authors, metadata, 1L))

        Given {
            contentType(APPLICATION_JSON_VALUE)
            body("""
            {
                "headline": "headline",
                "summary": "summary",
                "content": "content",
                "authors": [
                    {"username": "john.doe", "name": "John Doe"},
                    {"username": "jane.doe", "name": "Jane Doe"}
                ],
                "meta": {
                    "publishDate": "2019-01-18",
                    "tags": ["facebook", "image-compression", "gear", "internet"]
                }
            }
            """)
        } When {
            post(BASE_URL)
        } Then {
            statusCode(201)
        }
    }

    @Test
    fun `when publishing an article, if the contract was invalid, return status 400`() {
        Given {
            contentType(APPLICATION_JSON_VALUE)
            body("""
            {
                "headline": "headline",
                "summary": "summary",
                "content": "content",
                "authors": [
                    {"username": "john.doe", "name": "John Doe"},
                    {"username": "jane.doe", "name": "Jane Doe"}
                ],
                "publishDate": "2019-01-18",
                "tags": ["facebook", "image-compression", "gear", "internet"]
            }
            """)
        } When {
            post(BASE_URL)
        } Then {
            statusCode(400)
        }
    }

    @Test
    fun `when publishing an article, if entity couldn't be created, return status 422`() {
        Given {
            contentType(APPLICATION_JSON_VALUE)
            body("""
            {
                "headline": "headline",
                "summary": "summary",
                "content": "",
                "authors": [
                    {"username": "john.doe", "name": "John Doe"},
                    {"username": "jane.doe", "name": "Jane Doe"}
                ],
                "meta": {
                    "publishDate": "2019-01-18",
                    "tags": ["facebook", "image-compression", "gear", "internet"]
                }
            }
            """)
        } When {
            post(BASE_URL)
        } Then {
            statusCode(400)
        }
    }

    @Test
    fun `when retrieving an article, if it was found, return status 200`() {
        `when`(service.retrieve(anyLong()))
            .thenReturn(Article.builder
                .withId(1L)
                .withHeadline("headline")
                .withContent("content")
                .wthSummary("summary")
                .wthAuthors(authors)
                .withPubDate(metadata.publishDate)
                .withTags(metadata.tags)
                .build())

        Given {
            pathParam("id", 1L)
        } When {
            get("$BASE_URL/{id}")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun `when retrieving an article, if it was not found, return status 404`() {
        `when`(service.retrieve(anyLong()))
            .thenReturn(null)

        Given {
            pathParam("id", 1L)
        } When {
            get("$BASE_URL/{id}")
        } Then {
            statusCode(404)
        }
    }

    @Test
    fun `when updating an article, if found, return status 200`() {
        `when`(service.update(anyLong(), any()))
            .thenReturn(Article.builder
                .withHeadline("anotherHeadline")
                .wthSummary("anotherSummary")
                .withContent("anotherContent")
                .withPubDate(LocalDate.of(2019, 1, 18))
                .wthAuthors(mutableListOf(
                    Author("john.doe", "John Doe"),
                    Author("john.smith", "John Smith")
                ))
                .withTags(mutableListOf("facebook", "image-compression", "gear"))
                .withId(1L)
                .build())

        Given {
            pathParam("id", 1L)
            contentType(APPLICATION_JSON_VALUE)
            body("""
            {
                "headline": "anotherHeadline",
                "summary": "anotherSummary",
                "content": "anotherContent",
                "authors": [
                    {"username": "john.doe", "name": "John Doe"},
                    {"username": "john.smith", "name": "John Smith"}
                ],
                "meta": {
                    "publishDate": "2019-01-18",
                    "tags": ["facebook", "image-compression", "gear"]
                }
            }
            """)
        } When {
            put("$BASE_URL/{id}")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun `when updating an article, if not found, return status 404`() {
        `when`(service.update(anyLong(), any()))
            .thenReturn(null)

        Given {
            pathParam("id", 1L)
            contentType(APPLICATION_JSON_VALUE)
            body("""
            {
                "headline": "anotherHeadline",
                "summary": "anotherSummary",
                "content": "anotherContent",
                "authors": [
                    {"username": "john.doe", "name": "John Doe"},
                    {"username": "john.smith", "name": "John Smith"}
                ],
                "meta": {
                    "publishDate": "2019-01-18",
                    "tags": ["facebook", "image-compression", "gear"]
                }
            }
            """)
        } When {
            put("$BASE_URL/{id}")
        } Then {
            statusCode(404)
        }
    }

    @Test
    fun `when purging an article, if it was found, return status 200`() {
        doNothing().`when`(service).purge(anyLong())

        Given {
            pathParam("id", 1L)
        } When {
            delete("$BASE_URL/{id}")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun `when purging an article, if it was not found, return status 404`() {
        doThrow(EmptyResultDataAccessException::class.java)
            .`when`(service).purge(anyLong())

        Given {
            pathParam("id", 1L)
        } When {
            delete("$BASE_URL/{id}")
        } Then {
            statusCode(404)
        }
    }

    @Test
    fun `when searching an article, return a list of all that match the criteria, with status 200`() {
        `when`(service.search(
            authors = anyString(),
            tags = anyString(),
            afterDate = any<LocalDate>(),
            beforeDate = any<LocalDate>(),
            page = any()
        )).thenReturn(PageImpl(getArticles()))

        Given {
            noFilters()
        } When {
            get(BASE_URL)
        } Then {
            statusCode(200)
        }
    }

    private fun getArticles() = mutableListOf(
        Article.builder
            .withId(1L)
            .withHeadline("headline I")
            .withContent("content")
            .wthSummary("summary")
            .wthAuthors(mutableListOf(Author("martin.odersky", "Martin Odersky")))
            .withPubDate(LocalDate.parse("2019-01-10T13:14:00", ISO_DATE_TIME))
            .withTags(mutableListOf("scala", "functional-programming"))
            .build(),
        Article.builder
            .withId(2L)
            .withHeadline("headline II")
            .withContent("content")
            .wthSummary("summary")
            .wthAuthors(mutableListOf(Author("martin.thompson", "Martin Thompson")))
            .withPubDate(LocalDate.parse("2019-01-11T10:40:00", ISO_DATE_TIME))
            .withTags(mutableListOf("microservices", "low-latency", "performance", "java"))
            .build(),
        Article.builder
            .withId(3L)
            .withHeadline("headline III")
            .withContent("content")
            .wthSummary("summary")
            .wthAuthors(mutableListOf(Author("gayle", "Gayle McDowell")))
            .withPubDate(LocalDate.parse("2019-01-12T10:40:00", ISO_DATE_TIME))
            .withTags(mutableListOf("algorithms", "coding-interviews"))
            .build(),
        Article.builder
            .withId(4L)
            .withHeadline("headline IV")
            .withContent("content")
            .wthSummary("summary")
            .wthAuthors(mutableListOf(Author("jetbrains", "JetBrains Press")))
            .withPubDate(LocalDate.parse("2019-01-15T10:40:00", ISO_DATE_TIME))
            .withTags(mutableListOf("kotlin", "microservices"))
            .build(),
        Article.builder
            .withId(5L)
            .withHeadline("headline V")
            .withContent("content")
            .wthSummary("summary")
            .wthAuthors(mutableListOf(Author("martin.fowler", "Martin Fowler")))
            .withPubDate(LocalDate.parse("2019-01-16T10:40:00", ISO_DATE_TIME))
            .withTags(mutableListOf("design-patterns", "java", "microservices"))
            .build()
    )
}
