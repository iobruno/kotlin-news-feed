package io.petproject.model

data class Article(val headline: String,
                   val content: String,
                   val summary: String?,
                   val meta: ArticleMetadata) {

    init {
        require(headline.isNotBlank()) { "Headline must not be blank" }
        require(content.isNotBlank()) { "Content must not be blank" }
    }

}