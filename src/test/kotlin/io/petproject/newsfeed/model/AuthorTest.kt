package io.petproject.newsfeed.model

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

internal class AuthorTest {

    @Test
    fun `when username is blank, throw IllegalArgEx`() {
        assertThatThrownBy { Author(username = " ", name = "John Doe") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Username must not be blank")
    }

    @Test
    fun `when name is blank, throw IllegalArgEx`() {
        assertThatThrownBy { Author(username = "john.doe", name = " ") }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("Author name must not be blank")
    }

    @Test
    fun `when authors usernames are the same, they should be equal`() {
        val author = Author("username", "Name")
        val sameAuthor = Author("username", "Another Name")
        assertThat(author).isEqualTo(sameAuthor)
    }
}
