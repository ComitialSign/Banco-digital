package com.comtialsign.bancodigital.infra;

import com.comtialsign.bancodigital.dtos.ExceptionDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Lida com as exceçôes globais da RESTapi
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     *  Lida quando o usuário já está cadastrado no DB
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionDto> threatDyplicateEntity() {
        ExceptionDto exceptionDto = new ExceptionDto("Usuário já cadastrado", HttpStatus.CONFLICT);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exceptionDto);
    }

    /**
     * quando o usuário não foi encontrado no DB
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionDto> threatEntityNotFound() {
        ExceptionDto exceptionDto = new ExceptionDto("Usuário não encontrado", HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionDto);
    }

    /**
     *Lida com as exceções que não estão registradas aqui da aplicação
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDto> threatException(final Exception e) {
        ExceptionDto exceptionDto = new ExceptionDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionDto);
    }
}
