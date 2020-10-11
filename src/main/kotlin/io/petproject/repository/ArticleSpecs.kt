package io.petproject.repository

import io.petproject.model.Article
import org.springframework.data.jpa.domain.Specification
import java.time.LocalDate
import javax.persistence.criteria.*

object ArticleSpecs {

    fun withAuthors(authors: List<String>): Specification<Article> {
        return object : Specification<Article> {
            override fun toPredicate(root: Root<Article>, query: CriteriaQuery<*>, cb: CriteriaBuilder): Predicate? {
                val authorsPredicate = root.join<Any, Any>("meta").join<Any, Any>("authors")
                query.distinct(true)

                if (authors.isNotEmpty()) {
                    val predicates: List<Predicate> = authors.map {
                        cb.like(cb.lower(authorsPredicate.get("name")), "%${it.toLowerCase()}%")
                    }
                    return cb.or(*predicates.toTypedArray())
                }
                return null
            }
        }
    }

    fun withTags(tags: List<String>): Specification<Article> {
        return object : Specification<Article> {
            override fun toPredicate(root: Root<Article>, query: CriteriaQuery<*>, cb: CriteriaBuilder): Predicate? {
                val tagsPredicate = root.join<Any, Any>("meta").join<Any, Any>("tags")
                query.distinct(true)

                if (tags.isNotEmpty()) {
                    return tagsPredicate.`in`(tags)
                }
                return null
            }
        }
    }

    fun withDateAfter(afterDate: LocalDate?): Specification<Article> {
        return object : Specification<Article> {
            override fun toPredicate(root: Root<Article>, query: CriteriaQuery<*>, cb: CriteriaBuilder): Predicate? {
                val publishDatePredicate: Path<LocalDate> = root.join<Any, Any>("meta").get("publishDate")
                query.distinct(true)
                return afterDate?.let { cb.greaterThanOrEqualTo(publishDatePredicate, it) }
            }
        }
    }

    fun withDateBefore(beforeDate: LocalDate?): Specification<Article> {
        return object : Specification<Article> {
            override fun toPredicate(root: Root<Article>, query: CriteriaQuery<*>, cb: CriteriaBuilder): Predicate? {
                val publishDatePredicate: Path<LocalDate> = root.join<Any, Any>("meta").get("publishDate")
                query.distinct(true)
                return beforeDate?.let { cb.lessThan(publishDatePredicate, beforeDate) }
            }
        }
    }

}