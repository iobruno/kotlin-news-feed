package io.petproject.newsfeed.model

import com.fasterxml.jackson.annotation.JsonManagedReference
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import javax.persistence.*
import javax.persistence.CascadeType.*
import javax.persistence.GenerationType.AUTO

@Entity
@Table(name = "articles")
data class Article(
    @Column(name = "headline")
    @JsonProperty("headline")
    val headline: String,

    @Lob @Column(name = "content")
    @JsonProperty("content")
    val content: String,

    @Column(name = "summary")
    @JsonProperty("summary")
    val summary: String?,

    @ManyToMany(cascade = [PERSIST, DETACH, MERGE, REFRESH])
    @JoinTable(
        name = "authors_articles",
        joinColumns = [JoinColumn(name = "article_id")],
        inverseJoinColumns = [JoinColumn(name = "author_id")]
    )
    @JsonProperty("authors")
    @JsonManagedReference
    val authors: MutableList<Author>,

    @Embedded
    @JsonProperty("meta")
    val meta: ArticleMetadata,

    @Id @GeneratedValue(strategy = AUTO, generator = "article_seq_gen")
    @JsonProperty
    var id: Long? = null
) {

    init {
        require(headline.isNotBlank()) { "Headline must not be blank" }
        require(content.isNotBlank()) { "Content must not be blank" }
        require(authors.isNotEmpty()) { "There must be at least one author for the article" }
    }

    companion object {
        val builder = Builder()
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

        fun authors(authors: Iterable<Author>) = apply { this.authors = authors.toMutableList() }

        fun authors(authors: MutableList<Author>) = apply { this.authors = authors }

        fun tags(tags: MutableList<String>) = apply { this.tags = tags }

        fun id(id: Long) = apply { this.id = id }

        fun build() = Article(
            id = id,
            headline = headline,
            content = content,
            summary = summary,
            authors = authors,
            meta = ArticleMetadata(publishDate = publishDate, tags = tags)
        )
    }
}
