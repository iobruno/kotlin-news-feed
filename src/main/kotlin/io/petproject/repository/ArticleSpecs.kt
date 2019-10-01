package io.petproject.repository

import io.petproject.model.Article
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDate
import javax.persistence.criteria.*

object ArticleSpecs {

    fun withAuthors(authors: List<String>): Specification<Article> {
        TODO("implement specs for authors")
    }

    fun withTags(tags: List<String>): Specification<Article> {
        return object: Specification<Article> {
            override fun toPredicate(root: Root<Article>, query: CriteriaQuery<*>, cb: CriteriaBuilder): Predicate? {
                val tagsPredicate = root.join<Any, Any>("meta").join<Any, Any>("tags")
                query.distinct(true)
                return if (tags.isNotEmpty()) tagsPredicate.`in`(tags) else null
            }
        }
    }

    fun withDateAfter(afterDate: LocalDate?): Specification<Article> {
        return object: Specification<Article> {
            override fun toPredicate(root: Root<Article>, query: CriteriaQuery<*>, cb: CriteriaBuilder): Predicate? {
                val publishDatePredicate: Path<LocalDate> = root.join<Any, Any>("meta").get("publishDate")
                query.distinct(true)
                return afterDate?.let { cb.greaterThanOrEqualTo(publishDatePredicate, it) }
            }
        }
    }

    fun withDateBefore(beforeDate: LocalDate?): Specification<Article> {
        return object: Specification<Article> {
            override fun toPredicate(root: Root<Article>, query: CriteriaQuery<*>, cb: CriteriaBuilder): Predicate? {
                val publishDatePredicate: Path<LocalDate> = root.join<Any, Any>("meta").get("publishDate")
                query.distinct(true)
                return beforeDate?.let { cb.lessThan(publishDatePredicate, beforeDate) }
            }
        }
    }

}