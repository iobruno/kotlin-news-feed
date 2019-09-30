package io.petproject.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.NaturalId
import javax.persistence.*

@Entity
@Table(name = "authors")
data class Author(
        @NaturalId @Column(name = "username", unique = false)
        @JsonProperty("username") val username: String,

        @Column(name = "name")
        @JsonProperty("name") val name: String,

        @Id @GeneratedValue(strategy = GenerationType.AUTO, generator = "author_seq_gen")
        @JsonProperty var id: Long? = null) {

    @ManyToMany(mappedBy = "meta.authors", fetch = FetchType.LAZY)
    @JsonBackReference
    val articles: List<Article> = mutableListOf()

    init {
        require(username.isNotBlank()) { "Username must not be blank" }
        require(name.isNotBlank()) { "Author name must not be blank" }
    }

}