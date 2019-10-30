package io.petproject.config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory
import org.springframework.boot.web.server.ErrorPage
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
import org.springframework.http.HttpStatus

@Configuration
class ServletConfiguration {

    @Value("\${server.port:8080}")
    val serverPort: Int? = null

    @Bean
    fun webServerFactory(): ConfigurableServletWebServerFactory {
        val factory = JettyServletWebServerFactory()
        factory.port = serverPort!!
        factory.addErrorPages(ErrorPage(HttpStatus.NOT_FOUND, "/notfound.html"))
        return factory
    }

}