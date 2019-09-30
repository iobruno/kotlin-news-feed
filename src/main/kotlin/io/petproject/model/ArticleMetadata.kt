package io.petproject.model

import com.fasterxml.jackson.annotation.JsonManagedReference
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import javax.persistence.*

@Embeddable
data class ArticleMetadata(
        @Column(name = "publishDate")
        @JsonProperty("publishDate") val publishDate: LocalDate,

        @ElementCollection(fetch = FetchType.LAZY)
        @CollectionTable(name = "article_tags")
        @JsonProperty("tags") val tags: List<String>,

        @ManyToMany(cascade = [CascadeType.ALL])
        @JoinTable(name = "authors_articles",
            joinColumns = [JoinColumn(name = "article_id")],
            inverseJoinColumns = [JoinColumn(name = "author_id")]
        )
        @JsonProperty("authors")
        val authors: List<Author>) {

    init {
        require(tags.isNotEmpty()) { "There must be at least one tag label for the article" }
        require(authors.isNotEmpty()) { "There must be at least one author for the article" }
    }

}