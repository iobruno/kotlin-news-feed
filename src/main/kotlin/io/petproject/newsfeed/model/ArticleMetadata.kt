package io.petproject.newsfeed.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDate
import javax.persistence.*
import javax.persistence.FetchType.LAZY

@Embeddable
data class ArticleMetadata(
    @Column(name = "publishDate")
    @JsonProperty("publishDate")
    val publishDate: LocalDate,

    @CollectionTable(name = "article_tags")
    @ElementCollection(fetch = LAZY)
    @JsonProperty("tags")
    val tags: MutableList<String>
) {

    init {
        require(tags.isNotEmpty()) { "There must be at least one tag label for the article" }
    }
}
