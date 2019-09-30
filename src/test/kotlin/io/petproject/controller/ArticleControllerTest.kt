package io.petproject.controller

import io.petproject.model.Article
import io.petproject.model.ArticleMetadata
import io.petproject.model.Author
import io.petproject.service.ArticleService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
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
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

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
    fun `setup`() {
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
    fun `when publishing an article, if it was successful, return status 201`() {
        val jsonRequest = """
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
                .thenReturn(Article("headline", "content", "summary", metadata))

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jsonRequest))
                .andExpect(status().isCreated)
    }

    @Test
    fun `when publishing an article, if the contract was invalid, return status 400`() {
        val jsonRequest = """
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
                .content(jsonRequest))
                .andExpect(status().isBadRequest)
    }

    @Test
    fun `when publishing an article, if entity couldn't be created, return status 422`() {
        val noContentJsonRequest = """
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
                .content(noContentJsonRequest))
                .andExpect(status().isUnprocessableEntity)
    }

    @Test
    fun `when retrieving an article, if it was found, return status 200`() {
        `when`(service.retrieve(anyLong()))
            .thenReturn(Article("headline", "content", "summary", metadata))

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
    @Disabled
    fun `when searching an article, return a list of all that match the criteria, with status 200`() {
        TODO("not implemented")
    }

}