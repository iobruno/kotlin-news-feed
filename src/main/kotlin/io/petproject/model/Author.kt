package io.petproject.model

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonProperty
import org.hibernate.annotations.NaturalId
import java.lang.ClassCastException
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

    override fun equals(other: Any?): Boolean {
        return try {
            val that = other as Author
            this.username == that.username
        } catch(ex: ClassCastException) {
            false
        }
    }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + name.hashCode()
        return result
    }

}