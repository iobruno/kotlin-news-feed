package io.petproject.service

import io.petproject.model.Article
import io.petproject.repository.ArticleRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import javax.transaction.Transactional

@Service
@Transactional
class ArticleService @Autowired constructor(val articleRepo: ArticleRepository) {

    fun publish(article: Article) {
        TODO("persist article in DB")
    }

    fun retrieve(id: Long): Article? {
        TODO("retrieve article from DB")
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