package io.petproject.controller

import io.petproject.service.ArticleService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc

@ExtendWith(SpringExtension::class)
@WebMvcTest(ArticleController::class)
@AutoConfigureMockMvc
internal class ArticleControllerTest {

    @MockBean lateinit var service: ArticleService
    @Autowired lateinit var mockMvc: MockMvc

    @Test
    fun `when publishing an article, if it was successful, return status 201`() {
        TODO("not implemented")
    }

    @Test
    fun `when publishing an article, if the contract was invalid, return status 400`() {
        TODO("not implemented")
    }

    @Test
    fun `when publishing an article, if entity couldn't be created, return status 422`() {
        TODO("not implemented")
    }

    @Test
    fun `when retrieving an article, if it was found, return status 200`() {
        TODO("not implemented")
    }

    @Test
    fun `when retrieving an article, if it was not found, return status 404`() {
        TODO("not implemented")
    }

    @Test
    fun `when archiving an article, if it was found, return status 200`() {
        TODO("not implemented")
    }

    @Test
    fun `when archiving an article, if it was not found, return status 404`() {
        TODO("not implemented")
    }

    @Test
    fun `when searching an article, return a list of all that match the criteria, with status 200`() {
        TODO("not implemented")
    }

}