package io.petproject.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate

data class ArticleMetadata(@JsonProperty("publishDate") val publishDate: LocalDate,
                           @JsonProperty("tags") val tags: List<String>,
                           @JsonProperty("authors") val authors: List<Author>) {

    init {
        require(tags.isNotEmpty()) { "There must be at least one tag label for the article" }
        require(authors.isNotEmpty()) { "There must be at least one author for the article" }
    }

}