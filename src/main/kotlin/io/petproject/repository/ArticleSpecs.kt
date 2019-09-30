package io.petproject.repository

import io.petproject.model.Article
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDate

object ArticleSpecs {

    fun withAuthors(authors: List<String>): Specification<Article> {
        TODO("implement specs for authors")
    }

    fun withTags(tags: List<String>): Specification<Article> {
        TODO("implement specs for tags")
    }

    fun withDateAfter(date: LocalDate?): Specification<Article> {
        TODO("implement specs for dateAfter")
    }

    fun withDateBefore(date: LocalDate?): Specification<Article> {
        TODO("implement specs for dateBefore")
    }

}