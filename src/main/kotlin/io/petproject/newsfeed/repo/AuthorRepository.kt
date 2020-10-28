package io.petproject.newsfeed.repo

import io.petproject.newsfeed.model.Author
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
@Transactional(value = Transactional.TxType.MANDATORY)
interface AuthorRepository : JpaRepository<Author, Long> {

    fun findByUsername(username: String): Author?
}
