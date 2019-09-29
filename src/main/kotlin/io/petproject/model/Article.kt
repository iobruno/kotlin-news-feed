package io.petproject.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Article(@JsonProperty("headline") val headline: String,
                   @JsonProperty("content") val content: String,
                   @JsonProperty("summary") val summary: String?,
                   @JsonProperty("meta") val meta: ArticleMetadata) {

    init {
        require(headline.isNotBlank()) { "Headline must not be blank" }
        require(content.isNotBlank()) { "Content must not be blank" }
    }

}