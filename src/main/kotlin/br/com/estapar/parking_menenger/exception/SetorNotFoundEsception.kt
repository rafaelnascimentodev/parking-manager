package br.com.estapar.parking_menenger.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class SetorNotFoundEsception (message: String) : RuntimeException(message)