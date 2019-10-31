package io.petproject.model

import com.fasterxml.jackson.annotation.JsonManagedReference
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import javax.persistence.*
import javax.persistence.CascadeType.*

@Embeddable
data class ArticleMetadata(
        @Column(name = "publishDate")
        @JsonProperty("publishDate") val publishDate: LocalDate,

        @ElementCollection(fetch = FetchType.LAZY)
        @CollectionTable(name = "article_tags")
        @JsonProperty("tags") val tags: MutableList<String>,

        @ManyToMany(cascade = [PERSIST, DETACH, MERGE, REFRESH])
        @JoinTable(name = "authors_articles",
            joinColumns = [JoinColumn(name = "article_id")],
            inverseJoinColumns = [JoinColumn(name = "author_id")])
        @JsonManagedReference
        @JsonProperty("authors")
        val authors: MutableList<Author>) {

    init {
        require(tags.isNotEmpty()) { "There must be at least one tag label for the article" }
        require(authors.isNotEmpty()) { "There must be at least one author for the article" }
    }

}