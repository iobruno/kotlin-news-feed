package io.petproject.service

import io.petproject.model.Article
import io.petproject.model.ArticleMetadata
import io.petproject.model.Author
import io.petproject.repository.ArticleRepository
import io.petproject.repository.AuthorRepository
import org.springframework.beans.factory.annotation.Autowired
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

    fun search(authors: String, tags: String, startDate: LocalDate, endDate: LocalDate) {
        TODO("search articles matching parameters")
    }

    fun search(authors: List<String>, tags: List<String>,
               startDate: LocalDate, endDate: LocalDate) {
        TODO("search articles matching parameters")
    }

}