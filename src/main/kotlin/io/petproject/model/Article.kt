package io.petproject.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import javax.persistence.*
import javax.persistence.GenerationType.AUTO

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

        @Id @GeneratedValue(strategy = AUTO, generator = "article_seq_gen")
        @JsonProperty var id: Long? = null) {

    init {
        require(headline.isNotBlank()) { "Headline must not be blank" }
        require(content.isNotBlank()) { "Content must not be blank" }
    }

    class Builder {
        private lateinit var headline: String
        private lateinit var content: String
        private var summary: String? = null
        private lateinit var publishDate: LocalDate
        private lateinit var authors: MutableList<Author>
        private lateinit var tags: MutableList<String>
        private var id: Long? = null

        fun headline(headline: String) = apply { this.headline = headline }

        fun content(content: String) = apply { this.content = content }

        fun summary(summary: String?) = apply { this.summary = summary }

        fun publishDate(publishDate: LocalDate) = apply { this.publishDate = publishDate }

        fun authors(authors: MutableList<Author>) = apply { this.authors = authors }

        fun tags(tags: MutableList<String>) = apply { this.tags = tags }

        fun id(id: Long) = apply { this.id = id }

        fun build(): Article {
            return Article(headline = headline, content = content, summary = summary, id = id,
                    meta = ArticleMetadata(publishDate = publishDate, authors = authors, tags = tags))
        }
    }

}