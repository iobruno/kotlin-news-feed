package io.petproject.newsfeed.model

import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class ArticleMetadataTest {

    @Test
    fun `when tags is empty, it should throw IllegalArgEx`() {
        assertThatThrownBy {
            ArticleMetadata(publishDate = LocalDate.now(), tags = mutableListOf())
        }
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("There must be at least one tag label for the article")
    }
}
