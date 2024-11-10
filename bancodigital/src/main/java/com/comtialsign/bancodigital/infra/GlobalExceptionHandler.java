package com.comtialsign.bancodigital.infra;

import com.comtialsign.bancodigital.dtos.ExceptionDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ExceptionDto> threatDyplicateEntity() {
        ExceptionDto exceptionDto = new ExceptionDto("Usuário já cadastrado", HttpStatus.CONFLICT);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exceptionDto);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ExceptionDto> threatEntityNotFound() {
        ExceptionDto exceptionDto = new ExceptionDto("Usuário não encontrado", HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exceptionDto);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDto> threatException(final Exception e) {
        ExceptionDto exceptionDto = new ExceptionDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(exceptionDto);
    }
}
