package br.com.estapar.parking_mananger.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class VagaNotFoundEsception (message: String) : RuntimeException(message)