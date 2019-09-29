package io.petproject.model

data class Article(val headline: String,
                   val content: String,
                   val summary: String?,
                   val meta: ArticleMetadata) {

}