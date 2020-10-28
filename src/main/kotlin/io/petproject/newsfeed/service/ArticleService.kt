package io.petproject.newsfeed.service

import io.petproject.newsfeed.model.Article
import io.petproject.newsfeed.model.ArticleMetadata
import io.petproject.newsfeed.model.Author
import io.petproject.newsfeed.repo.ArticleRepository
import io.petproject.newsfeed.repo.ArticleSpecs.withAuthors
import io.petproject.newsfeed.repo.ArticleSpecs.withDateAfter
import io.petproject.newsfeed.repo.ArticleSpecs.withDateBefore
import io.petproject.newsfeed.repo.ArticleSpecs.withTags
import io.petproject.newsfeed.repo.AuthorRepository
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

    fun retrieve(id: Long): Article? = articleRepo.findByIdOrNull(id)

    fun purge(id: Long) = articleRepo.deleteById(id)

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

    fun update(id: Long, updatingArticle: Article): Article? {
        val foundArticle = retrieve(id)
        return foundArticle?.let {
            val authors = findOrSaveAuthors(updatingArticle.authors)
            val updatedArticle: Article =
                Article.builder
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

    fun search(
        authors: List<String>,
        tags: List<String>,
        afterDate: LocalDate?,
        beforeDate: LocalDate?,
        page: Pageable = PageRequest.of(0, 10)
    ): Page<Article> {

        val specs: Specification<Article> =
            Specification
                .where(withAuthors(authors))!!
                .and(withTags(tags))!!
                .and(withDateAfter(afterDate))!!
                .and(withDateBefore(beforeDate))!!

        return articleRepo.findAll(specs, page)
    }

    fun search(
        authors: String?,
        tags: String?,
        afterDate: LocalDate?,
        beforeDate: LocalDate?,
        page: Pageable = PageRequest.of(0, 10)
    ): Page<Article> {

        return search(
            authors = sanitizeSearchString(authors),
            tags = sanitizeSearchString(tags),
            afterDate = afterDate,
            beforeDate = beforeDate
        )
    }

    private fun sanitizeSearchString(string: String?): List<String> =
        string?.let { str -> str.split(",").map { it.trim() } } ?: listOf()

    private fun findOrSaveAuthors(authors: Iterable<Author>): MutableList<Author> =
        authors.map {
            authorRepo.findByUsername(it.username) ?: authorRepo.saveAndFlush(it)
        }.toMutableList()
}
