package io.petproject.model

import org.assertj.core.api.Assertions.assertThat
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

    @Test
    fun `when authors usernames are the same, they should be equal`() {
        val author = Author("username", "Name")
        val sameAuthor = Author("username", "Another Name")
        assertThat(author).isEqualTo(sameAuthor)
    }

}