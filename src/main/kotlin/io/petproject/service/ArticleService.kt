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
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.LocalDate
import javax.transaction.Transactional

@Service
@Transactional
class ArticleService @Autowired constructor(val articleRepo: ArticleRepository,
                                            val authorRepo: AuthorRepository) {

    fun publish(article: Article): Article {
        val authors: List<Author> = article.meta.authors.map {
            authorRepo.findByUsername(it.username) ?:
            authorRepo.saveAndFlush(it)
        }
        val document = Article(
            headline = article.headline, content = article.content, summary = article.summary,
            meta = ArticleMetadata(article.meta.publishDate, article.meta.tags, authors)
        )
        return articleRepo.save(document)
    }

    fun retrieve(id: Long): Article? {
        return articleRepo.findByIdOrNull(id)
    }

    fun archive(id: Long): Boolean {
        TODO("archive/soft-delete article from DB")
    }

    fun purge(id: Long) {
        articleRepo.deleteById(id)
    }

    fun search(authors: String?, tags: String?, afterDate: LocalDate?, beforeDate: LocalDate?): List<Article> {
        return search(
                authors = sanitizeSearchString(authors),
                tags = sanitizeSearchString(tags),
                afterDate = afterDate,
                beforeDate = beforeDate
        )
    }

    fun search(authors: List<String>, tags: List<String>,
               afterDate: LocalDate?, beforeDate: LocalDate?): List<Article> {

        val specs = Specification.where(withAuthors(authors))
                .and(withTags(tags))
                .and(withDateAfter(afterDate))
                .and(withDateBefore(beforeDate))

        return articleRepo.findAll(specs)
    }

    private fun sanitizeSearchString(string: String?): List<String> {
        return string?.let { s -> s.split(",").map { it.trim() } } ?: listOf()
    }

}