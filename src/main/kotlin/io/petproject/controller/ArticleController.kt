package io.petproject.controller

import io.petproject.model.Article
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/v1/articles")
class ArticleController {

    @PostMapping
    fun publish(@RequestBody article: Article) {
        TODO("implement persistence")
    }

    @GetMapping
    fun search(@RequestParam("authors") authors: String,
               @RequestParam("tags") tags: String,
               @RequestParam("fromDate") fromDate: LocalDate,
               @RequestParam("toDate") toDate: LocalDate) {
        TODO("implement search")
    }

    @GetMapping("/{id}")
    fun retrieve(@PathVariable("id") id: Long) {
        TODO("implement retrieval")
    }

    @DeleteMapping("/{id}")
    fun archive(@PathVariable("id") id: Long) {
        TODO("implement soft delete")
    }

}