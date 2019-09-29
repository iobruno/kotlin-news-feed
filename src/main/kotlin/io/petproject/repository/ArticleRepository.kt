package io.petproject.repository

import io.petproject.model.Article
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
@Transactional(value = Transactional.TxType.MANDATORY)
interface ArticleRepository : JpaRepository<Article, Long>