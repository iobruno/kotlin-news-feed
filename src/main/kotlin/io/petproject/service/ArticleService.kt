package io.petproject.service

import io.petproject.model.Article
import io.petproject.model.ArticleMetadata
import io.petproject.model.Author
import io.petproject.repository.ArticleRepository
import io.petproject.repository.ArticleSpecs.withAuthors
import io.petproject.repository.ArticleSpecs.withDateAfter
import io.petproject.repository.ArticleSpecs.withDateBefore
import io.petproject.repository.ArticleSpecs.withTags
import io.petproject.repository.AuthorRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDate
import javax.transaction.Transactional

@Service
@Transactional
class ArticleService @Autowired constructor(
    val articleRepo: ArticleRepository,
    val authorRepo: AuthorRepository
) {

    fun publish(article: Article): Article {
        val authors = findOrSaveAuthors(article.authors)
        val document = Article(
                headline = article.headline,
                content = article.content,
                summary = article.summary,
                authors = authors,
                meta = ArticleMetadata(article.meta.publishDate, article.meta.tags)
        )
        return articleRepo.save(document)
    }

    fun retrieve(id: Long): Article? {
        return articleRepo.findByIdOrNull(id)
    }

    fun update(id: Long, updatingArticle: Article): Article? {
        val foundArticle = retrieve(id)
        return foundArticle?.let {
            val authors = findOrSaveAuthors(updatingArticle.authors)
            val updatedArticle = Article.Builder()
                    .headline(updatingArticle.headline)
                    .content(updatingArticle.content)
                    .summary(updatingArticle.summary)
                    .publishDate(updatingArticle.meta.publishDate)
                    .tags(updatingArticle.meta.tags)
                    .authors(authors)
                    .id(foundArticle.id!!)
                    .build()

            articleRepo.save(updatedArticle)
        }
    }

    fun purge(id: Long) {
        articleRepo.deleteById(id)
    }

    fun search(authors: List<String>, tags: List<String>,
               afterDate: LocalDate?, beforeDate: LocalDate?,
               page: Pageable = PageRequest.of(0, 10)): Page<Article> {

        val specs = Specification.where(withAuthors(authors))!!
                .and(withTags(tags))!!
                .and(withDateAfter(afterDate))!!
                .and(withDateBefore(beforeDate))

        return articleRepo.findAll(specs, page)
    }

    fun search(authors: String?, tags: String?,
               afterDate: LocalDate?, beforeDate: LocalDate?,
               page: Pageable = PageRequest.of(0, 10)): Page<Article> {

        return search(authors = sanitizeSearchString(authors), tags = sanitizeSearchString(tags),
                afterDate = afterDate, beforeDate = beforeDate, page = page)
    }

    private fun sanitizeSearchString(string: String?): List<String> {
        return string?.let { s -> s.split(",").map { it.trim() } } ?: listOf()
    }

    private fun findOrSaveAuthors(authors: Iterable<Author>): MutableList<Author> {
        return authors.map {
            authorRepo.findByUsername(it.username) ?: authorRepo.saveAndFlush(it)
        }.toMutableList()
    }

}
