package io.petproject.model

import java.time.LocalDate

data class ArticleMetadata(val publishDate: LocalDate,
                           val tags: List<String>,
                           val authors: List<Author>) {

    init {
        require(tags.isNotEmpty()) { "There must be at least one tag label for the article" }
        require(authors.isNotEmpty()) { "There must be at least one author for the article" }
    }

}