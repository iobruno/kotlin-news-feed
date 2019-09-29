package io.petproject.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException

internal class AuthorTest {

    @Test
    fun `when username is blank, throw IllegalArgEx`() {
        assertThrows(IllegalArgumentException::class.java) {
            Author(username = " ", name = "John Doe")        }
    }

    @Test
    fun `when name is blank, throw IllegalArgEx`() {
        assertThrows(IllegalArgumentException::class.java) {
            Author(username = "john.doe", name = " ")
        }
    }

}