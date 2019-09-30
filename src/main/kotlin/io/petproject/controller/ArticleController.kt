package io.petproject.controller

import com.fasterxml.jackson.databind.exc.InvalidDefinitionException
import io.petproject.model.Article
import io.petproject.service.ArticleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/articles")
class ArticleController @Autowired constructor(private val service: ArticleService) {

    @PostMapping
    fun publish(@RequestBody article: Article): ResponseEntity<Article> {
        val publishedArticle = service.publish(article)
        return ResponseEntity(publishedArticle, HttpStatus.CREATED)
    }

    @GetMapping
    fun search(@RequestParam("authors") authors: String,
               @RequestParam("tags") tags: String,
               @RequestParam("fromDate") fromDate: LocalDate,
               @RequestParam("toDate") toDate: LocalDate) {
        TODO("implement search")
    }

    @GetMapping("/{id}")
    fun retrieve(@PathVariable("id") id: Long): ResponseEntity<Article> {
        val article: Article? = service.retrieve(id)
        return article?.let { ResponseEntity.ok(it) } ?: ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{id}")
    fun archive(@PathVariable("id") id: Long): ResponseEntity<Any> {
        if (service.archive(id)) {
            return ResponseEntity.ok().build()
        }
        return ResponseEntity.notFound().build()
    }

    @ExceptionHandler(value = [InvalidDefinitionException::class])
    fun handleUnprocessableEntity(): ResponseEntity<Any> {
        return ResponseEntity.unprocessableEntity().build()
    }

}