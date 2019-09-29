package io.petproject.model

import com.fasterxml.jackson.annotation.JsonProperty
import javax.persistence.*

@Entity
@Table(name = "articles")
data class Article(
        @Column(name = "headline")
        @JsonProperty("headline") val headline: String,

        @Column(name = "content") @Lob
        @JsonProperty("content") val content: String,

        @Column(name = "summary")
        @JsonProperty("summary") val summary: String?,

        @Embedded
        @JsonProperty("meta") val meta: ArticleMetadata,

        @Id @GeneratedValue(strategy = GenerationType.AUTO, generator = "article_seq_gen")
        @JsonProperty var id: Long? = null) {

    init {
        require(headline.isNotBlank()) { "Headline must not be blank" }
        require(content.isNotBlank()) { "Content must not be blank" }
    }

}