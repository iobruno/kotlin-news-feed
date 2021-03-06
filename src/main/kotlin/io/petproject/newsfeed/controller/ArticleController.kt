package io.petproject.newsfeed.controller

import io.petproject.newsfeed.model.Article
import io.petproject.newsfeed.service.ArticleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.format.annotation.DateTimeFormat.ISO.DATE
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/articles")
class ArticleController @Autowired constructor(private val service: ArticleService) {

    @PostMapping
    fun publish(@RequestBody article: Article): ResponseEntity<Article> {
        val publishedArticle = service.publish(article)
        return ResponseEntity.status(HttpStatus.CREATED).body(publishedArticle)
    }

    @GetMapping
    fun search(
        @RequestParam("authors") authors: String?,
        @RequestParam("tags") tags: String?,
        @RequestParam("publishedAfter") @DateTimeFormat(iso = DATE) publishedAfter: LocalDate?,
        @RequestParam("publishedBefore") @DateTimeFormat(iso = DATE) publishedBefore: LocalDate?,
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("pageSize", defaultValue = "10") pageSize: Int
    ): ResponseEntity<Page<Article>> {
        val pageable = PageRequest.of(page, pageSize)
        val searchResults = service.search(authors, tags, publishedAfter, publishedBefore, pageable)
        return ResponseEntity.ok(searchResults)
    }

    @GetMapping("/{id}")
    fun retrieve(@PathVariable("id") id: Long): ResponseEntity<Article> {
        return service.retrieve(id)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable("id") id: Long,
        @RequestBody updatingArticle: Article
    ): ResponseEntity<Article> {
        return service.update(id, updatingArticle)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    @DeleteMapping("/{id}")
    fun purge(@PathVariable("id") id: Long): ResponseEntity<Article> {
        return try {
            service.purge(id)
            ResponseEntity.ok().build()
        } catch (ex: EmptyResultDataAccessException) {
            ResponseEntity.notFound().build()
        }
    }

    @ExceptionHandler(value = [HttpMessageNotReadableException::class])
    fun handleMessageNotReadable(): ResponseEntity<Any> =
        ResponseEntity.badRequest().build()

}
