package br.com.estapar.parking_mananger.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
class ApplicationConfig {

    @Bean
    fun restTemplate(): RestTemplate = RestTemplate()
}