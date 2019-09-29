package io.petproject.model

data class Author(val username: String,
                  val name: String) {

    init {
        require(username.isNotBlank()) { "Username must not be blank" }
        require(name.isNotBlank()) { "Author name must not be blank" }
    }

}