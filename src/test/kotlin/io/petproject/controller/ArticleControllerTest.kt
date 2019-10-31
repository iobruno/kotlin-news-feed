package io.petproject.controller

import io.petproject.model.Article
import io.petproject.model.ArticleMetadata
import io.petproject.model.Author
import io.petproject.service.ArticleService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.domain.PageImpl
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@ExtendWith(SpringExtension::class)
@WebMvcTest(ArticleController::class)
@AutoConfigureMockMvc
internal class ArticleControllerTest {

    @MockBean private lateinit var service: ArticleService
    @Autowired private lateinit var mockMvc: MockMvc

    private lateinit var metadata: ArticleMetadata

    companion object {
        const val BASE_URL = "/api/v1/articles"
    }

    private fun <T> any(): T {
        return Mockito.any<T>()
    }

    @BeforeEach
    fun setup() {
        metadata = ArticleMetadata(
                publishDate = LocalDate.now(),
                tags = mutableListOf("first-tag", "second-tag"),
                authors = mutableListOf(
                    Author("john.doe", "John Doe", 1L),
                    Author("jane.doe", "Jane Doe", 2L)
                ))
    }

    @Test
    fun `when publishing an article, if it was successful, return status 201`() {
        val payload = """
        {
	        "headline": "headline",
	        "summary": "summary",
	        "content": "content",
	        "meta": {
		        "publishDate": "2019-01-18",
		        "authors": [
			        {"username": "john.doe", "name": "John Doe"},
			        {"username": "jane.doe", "name": "Jane Doe"}
		        ],
		        "tags": ["facebook", "image-compression", "gear", "internet"]
	        }
        }
        """

        `when`(service.publish(any()))
                .thenReturn(Article("headline", "content", "summary", metadata, 1L))

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(payload))
                .andExpect(status().isCreated)
    }

    @Test
    fun `when publishing an article, if the contract was invalid, return status 400`() {
        val payload = """
        {
	        "headline": "headline",
	        "summary": "summary",
	        "content": "content",
            "publishDate": "2019-01-18",
            "authors": [
                {"username": "john.doe", "name": "John Doe"},
                {"username": "jane.doe", "name": "Jane Doe"}
            ],
            "tags": ["facebook", "image-compression", "gear", "internet"]
        }
        """

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(payload))
                .andExpect(status().isBadRequest)
    }

    @Test
    fun `when publishing an article, if entity couldn't be created, return status 422`() {
        val noContentPayload = """
        {
	        "headline": "headline",
	        "summary": "summary",
	        "content": "",
	        "meta": {
		        "publishDate": "2019-01-18",
		        "authors": [
			        {"username": "john.doe", "name": "John Doe"},
			        {"username": "jane.doe", "name": "Jane Doe"}
		        ],
		        "tags": ["facebook", "image-compression", "gear", "internet"]
	        }
        }
        """

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(noContentPayload))
                .andExpect(status().isUnprocessableEntity)
    }

    @Test
    fun `when retrieving an article, if it was found, return status 200`() {
        `when`(service.retrieve(anyLong()))
            .thenReturn(Article("headline", "content", "summary", metadata, 1L))

        mockMvc.perform(get("$BASE_URL/{id}", 1L))
                .andExpect(status().isOk)
    }

    @Test
    fun `when retrieving an article, if it was not found, return status 404`() {
        `when`(service.retrieve(anyLong()))
                .thenReturn(null)

        mockMvc.perform(get("$BASE_URL/{id}", 1L))
                .andExpect(status().isNotFound)
    }

    @Test
    fun `when updating an article, if found, return status 200`() {
        val placeId = 4L
        val payload = """
        {
	        "headline": "anotherHeadline",
	        "summary": "anotherSummary",
	        "content": "anotherContent",
	        "meta": {
		        "publishDate": "2019-01-18",
		        "authors": [
			        {"username": "john.doe", "name": "John Doe"},
			        {"username": "john.smith", "name": "John Smith"}
		        ],
		        "tags": ["facebook", "image-compression", "gear"]
	        }
        }
        """

        `when`(service.update(anyLong(), any()))
                .thenReturn(Article.Builder()
                        .headline("anotherHeadline")
                        .summary("anotherSummary")
                        .content("anotherContent")
                        .publishDate(LocalDate.of(2019, 1, 18))
                        .authors(mutableListOf(
                                Author("john.doe", "John Doe"),
                                Author("john.smith", "John Smith")
                        ))
                        .tags(mutableListOf("facebook", "image-compression", "gear"))
                        .id(placeId)
                        .build()
                )

        mockMvc.perform(put("$BASE_URL/{id}", placeId)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(payload))
                .andExpect(status().isOk)
    }

    @Test
    fun `when updating an article, if not found, return status 404`() {
        val payload = """
        {
	        "headline": "anotherHeadline",
	        "summary": "anotherSummary",
	        "content": "anotherContent",
	        "meta": {
		        "publishDate": "2019-01-18",
		        "authors": [
			        {"username": "john.doe", "name": "John Doe"},
			        {"username": "john.smith", "name": "John Smith"}
		        ],
		        "tags": ["facebook", "image-compression", "gear"]
	        }
        }
        """

        `when`(service.update(anyLong(), any()))
                .thenReturn(null)

        mockMvc.perform(put("$BASE_URL/{id}", Long.MAX_VALUE)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(payload))
                .andExpect(status().isNotFound)
    }

    @Test
    fun `when purging an article, if it was found, return status 200`() {
        doNothing()
                .`when`(service).purge(anyLong())

        mockMvc.perform(delete("$BASE_URL/{id}", 1L))
                .andExpect(status().isOk)
    }

    @Test
    fun `when purging an article, if it was not found, return status 404`() {
        doThrow(EmptyResultDataAccessException::class.java)
                .`when`(service).purge(anyLong())

        mockMvc.perform(delete("$BASE_URL/{id}", 1L))
                .andExpect(status().isNotFound)
    }

    @Test
    fun `when searching an article, return a list of all that match the criteria, with status 200`() {
        `when`(service.search(
                authors = any<String?>(),
                tags = any<String?>(),
                afterDate = any(),
                beforeDate = any(),
                page = any()
        )).thenReturn(PageImpl(getArticles()))

        mockMvc.perform(get("$BASE_URL/"))
                .andExpect(status().isOk)
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