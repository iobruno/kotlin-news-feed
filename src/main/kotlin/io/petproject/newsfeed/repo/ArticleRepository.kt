package io.petproject.newsfeed.repo

import io.petproject.newsfeed.model.Article
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
@Transactional(value = Transactional.TxType.MANDATORY)
interface ArticleRepository : JpaRepository<Article, Long>, JpaSpecificationExecutor<Article>
