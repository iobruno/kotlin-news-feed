package io.petproject.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Author(@JsonProperty("username") val username: String,
                  @JsonProperty("name") val name: String) {

    init {
        require(username.isNotBlank()) { "Username must not be blank" }
        require(name.isNotBlank()) { "Author name must not be blank" }
    }

}