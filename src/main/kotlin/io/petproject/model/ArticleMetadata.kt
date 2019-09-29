package io.petproject.model

import java.time.LocalDate

data class ArticleMetadata(val publishDate: LocalDate,
                           val tags: List<String>,
                           val authors: List<Author>) {

}